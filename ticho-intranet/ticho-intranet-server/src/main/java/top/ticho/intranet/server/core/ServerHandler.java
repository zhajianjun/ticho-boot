package top.ticho.intranet.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.exception.IntranetException;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.filter.AppListenFilter;
import top.ticho.intranet.server.support.ApplicationSupport;
import top.ticho.intranet.server.support.ClientSupport;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端处理器
 *
 * @author zhajianjun
 * @date 2025-05-18 11:21
 */
@Slf4j
public record ServerHandler(
    AtomicInteger serverStatus,
    NioEventLoopGroup serverBoss,
    NioEventLoopGroup serverWorker,
    ServerProperty serverProperty,
    ServerBootstrap serverBootstrap,
    ServerBootstrap sslServerBootstrap,
    ClientSupport clientSupport,
    ApplicationSupport applicationSupport,
    AppListenFilter appListenFilter
) {

    public void init() {
        //  初始化服务端
        if (serverStatus.get() != ServerStatus.INITING.getCode()) {
            return;
        }
        try {
            log.info("内网映射服务初始化中，端口：{}，是否开启ssl：{}, ssl端口：{}", serverProperty.getPort(), serverProperty.getSslEnable(), serverProperty.getSslPort());
            int servPort = serverProperty.getPort();
            String host = CommConst.LOCALHOST;
            serverBootstrap.bind(host, servPort).get();
            if (Objects.nonNull(sslServerBootstrap)) {
                // 创建ssl服务端
                Integer sslServerPort = serverProperty.getSslPort();
                ChannelFuture cf = sslServerBootstrap.bind(host, sslServerPort);
                cf.sync();
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("内网映射服务初始化失败");
            throw new IntranetException("内网映射服务初始化失败", e);
        }
        serverStatus.set(ServerStatus.STARTING.getCode());
        log.info("内网映射服务初始化成功");
    }

    public void enable() {
        serverStatus.set(ServerStatus.ENABLED.getCode());
    }

    public void disable() {
        serverStatus.set(ServerStatus.DISABLED.getCode());
    }

    public void checkStatus() {
        if (serverStatus.get() == ServerStatus.DISABLED.getCode()) {
            throw new IntranetException("内网映射服务操作已禁用，请稍后再试");
        }
    }

    public boolean create(String accessKey, String name) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        return clientSupport.create(accessKey, name);
    }

    /**
     * 根据accessKey删除客户端
     */
    public void remove(String accessKey) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        remove(clientInfo);
    }

    /**
     * 删除所有客户端
     */
    public void removeAll() {
        checkStatus();
        findAll().forEach(this::remove);
    }

    private void remove(ClientInfo clientInfo) {
        checkStatus();
        // 删除应用
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        Optional.ofNullable(portMap)
            .map(Map::keySet)
            .ifPresent(ports -> {
                ports.forEach(applicationSupport::unbind);
                portMap.clear();
            });
        // 关闭请求通道
        clientSupport.closeRequestChannel(clientInfo);
        clientSupport.deleteClient(clientInfo.getAccessKey());
    }

    /**
     * 绑定应用
     */
    public boolean bind(String accessKey, Integer port, String endpoint) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        IntranetUtil.isNotNull(port, "port不能为空");
        IntranetUtil.isNotEmpty(endpoint, "endpoint不能为空");
        boolean matches = endpoint.matches("\\b(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?):([1-9]|[1-9][0-9]{1,3}|[1-5][0-9]{4}|6[0-5][0-5][0-3][0-5])\\b");
        IntranetUtil.isTrue(matches, "endpoint格式错误，格式[ip:port]");
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return false;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        boolean bind = applicationSupport.bind(port);
        if (bind) {
            portMap.put(port, new PortInfo(accessKey, port, endpoint));
        }
        return bind;
    }

    /**
     * 刷新客户端的应用
     */
    public void flush(String accessKey, Map<Integer, PortInfo> portInfoMap) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        if (Objects.isNull(portInfoMap)) {
            return;
        }
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMapFromMem = clientInfo.getPortMap();
        // 解绑portInfoMap中不存在，而portMapFromMem存在的端口
        portMapFromMem.values()
            .forEach(x -> {
                if (portInfoMap.containsKey(x.getPort())) {
                    return;
                }
                unbind(accessKey, x.getPort());
            });
        // 绑定portInfoMap中存在，而portMapFromMem不存在的端口
        portInfoMap.values()
            .forEach(x -> {
                if (!portMapFromMem.containsKey(x.getPort())) {
                    return;
                }
                bind(accessKey, x.getPort(), x.getEndpoint());
            });
    }

    /**
     * 根据accessKey解绑应用
     */
    public boolean unbind(String accessKey) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return false;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        boolean allUnbind = portMap.keySet()
            .stream()
            .allMatch(applicationSupport::unbind);
        portMap.clear();
        return allUnbind;
    }

    /**
     * 根据accessKey和端口号删除应用
     */
    public void unbind(String accessKey, Integer port) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        IntranetUtil.isNotNull(port, "port不能为空");
        Optional<ClientInfo> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        if (Objects.isNull(portMap) || !portMap.containsKey(port)) {
            return;
        }
        PortInfo portInfo = portMap.get(port);
        if (applicationSupport.unbind(portInfo.getPort())) {
            portMap.remove(port);
        }
    }

    public boolean exists(Integer portNum) {
        checkStatus();
        return applicationSupport.exists(portNum);
    }

    public Optional<ClientInfo> findByAccessKey(String accessKey) {
        checkStatus();
        return clientSupport.findByAccessKey(accessKey);
    }

    public List<ClientInfo> findAll() {
        checkStatus();
        return clientSupport.findAll();
    }

}
