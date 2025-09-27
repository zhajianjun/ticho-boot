package top.ticho.intranet.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.exception.IntranetException;
import top.ticho.intranet.common.prop.IntranetServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.entity.IntranetClient;
import top.ticho.intranet.server.entity.IntranetPort;
import top.ticho.intranet.server.filter.IntranetApplicationListenFilter;
import top.ticho.intranet.server.support.IntranetApplicationSupport;
import top.ticho.intranet.server.support.IntranetClientSupport;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiStrUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 服务端处理器
 *
 * @author zhajianjun
 * @date 2025-05-18 11:21
 */
@Slf4j
public record IntranetServerHandler(
    AtomicInteger serverStatus,
    NioEventLoopGroup serverBoss,
    NioEventLoopGroup serverWorker,
    IntranetServerProperty intranetServerProperty,
    ServerBootstrap serverBootstrap,
    ServerBootstrap sslServerBootstrap,
    IntranetClientSupport intranetClientSupport,
    IntranetApplicationSupport intranetApplicationSupport,
    IntranetApplicationListenFilter intranetApplicationListenFilter
) {

    public void init() {
        //  初始化服务端
        if (serverStatus.get() != ServerStatus.INITING.getCode()) {
            return;
        }
        try {
            log.info("内网映射服务初始化中，端口：{}，是否开启ssl：{}, ssl端口：{}", intranetServerProperty.getPort(), intranetServerProperty.getSslEnable(), intranetServerProperty.getSslPort());
            int servPort = intranetServerProperty.getPort();
            String host = CommConst.LOCALHOST;
            serverBootstrap.bind(host, servPort).get();
            if (Objects.nonNull(sslServerBootstrap)) {
                // 创建ssl服务端
                Integer sslServerPort = intranetServerProperty.getSslPort();
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
        return intranetClientSupport.create(accessKey, name);
    }

    /**
     * 根据accessKey删除客户端
     */
    public void remove(String accessKey) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        remove(intranetClient);
    }

    /**
     * 删除所有客户端
     */
    public void removeAll() {
        checkStatus();
        findAll().forEach(this::remove);
    }

    private void remove(IntranetClient intranetClient) {
        checkStatus();
        // 删除应用
        Map<Integer, IntranetPort> portMap = intranetClient.getPortMap();
        Optional.ofNullable(portMap)
            .map(Map::keySet)
            .ifPresent(ports -> {
                ports.forEach(intranetApplicationSupport::unbind);
                portMap.clear();
            });
        // 关闭请求通道
        intranetClientSupport.closeRequestChannel(intranetClient);
        intranetClientSupport.deleteClient(intranetClient.getAccessKey());
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
        Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            log.warn("绑定应用[{}]失败，客户端：{}不存在", port, accessKey);
            return false;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        Map<Integer, IntranetPort> portMap = intranetClient.getPortMap();
        boolean bind = intranetApplicationSupport.bind(port);
        if (bind) {
            portMap.put(port, new IntranetPort(accessKey, port, endpoint));
        }
        return bind;
    }

    public void flush(List<IntranetClient> intranetClients) {
        if (TiCollUtil.isEmpty(intranetClients)) {
            return;
        }
        Map<String, IntranetClient> clientInfoMap = intranetClients
            .stream()
            .filter(item -> TiStrUtil.isNotBlank(item.getAccessKey()))
            .collect(Collectors.toMap(IntranetClient::getAccessKey, Function.identity(), (v1, v2) -> v1));
        // 移除需要删除的client
        List<IntranetClient> intranetClientInfosFromMem = findAll();
        intranetClientInfosFromMem.forEach(clientInfoFromMem -> {
            if (clientInfoMap.containsKey(clientInfoFromMem.getAccessKey())) {
                return;
            }
            remove(clientInfoFromMem);
        });
        clientInfoMap.forEach((accessKey, clientInfo) -> {
            Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
            if (clientInfoOpt.isEmpty()) {
                create(accessKey, clientInfo.getName());
            }
            clientInfoOpt = findByAccessKey(accessKey);
            if (clientInfoOpt.isEmpty()) {
                return;
            }
            IntranetClient intranetClientFromMem = clientInfoOpt.get();
            Map<Integer, IntranetPort> portMapFromMem = new HashMap<>(intranetClientFromMem.getPortMap());
            Map<Integer, IntranetPort> portMap = Optional.ofNullable(clientInfo.getPortMap())
                .map(HashMap::new)
                .orElseGet(HashMap::new);
            // 解绑portInfoMap中不存在，而portMapFromMem存在的端口
            portMapFromMem.values().forEach(intranetPortFromMem -> {
                if (portMap.containsKey(intranetPortFromMem.getPort())) {
                    return;
                }
                unbind(accessKey, intranetPortFromMem.getPort());
            });
            // 绑定portInfoMap中存在，而portMapFromMem不存在的端口
            portMap.values().forEach(intranetPort -> {
                if (portMapFromMem.containsKey(intranetPort.getPort())) {
                    return;
                }
                bind(accessKey, intranetPort.getPort(), intranetPort.getEndpoint());
            });
        });
    }

    /**
     * 根据accessKey解绑应用
     */
    public boolean unbind(String accessKey) {
        checkStatus();
        IntranetUtil.isNotEmpty(accessKey, "accessKey不能为空");
        Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            log.warn("解绑客户端[{}]下所有应用失败，客户端不存在", accessKey);
            return false;
        }
        IntranetClient intranetClient = clientInfoOpt.get();
        Map<Integer, IntranetPort> portMap = intranetClient.getPortMap();
        boolean allUnbind = portMap.keySet()
            .stream()
            .allMatch(intranetApplicationSupport::unbind);
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
        Optional<IntranetClient> clientInfoOpt = findByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            log.warn("解绑应用[{}]失败，客户端：{}不存在", port, accessKey);
            return;
        }
        if (intranetApplicationSupport.unbind(port)) {
            clientInfoOpt.get().getPortMap().remove(port);
        }
        log.warn("解绑应用[{}]失败，客户端：{}不存在", port, accessKey);
    }

    public boolean exists(Integer portNum) {
        checkStatus();
        return intranetApplicationSupport.exists(portNum);
    }

    public Optional<IntranetClient> findByAccessKey(String accessKey) {
        checkStatus();
        return intranetClientSupport.findByAccessKey(accessKey);
    }

    public List<IntranetClient> findAll() {
        checkStatus();
        return intranetClientSupport.findAll();
    }

}
