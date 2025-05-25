package top.ticho.intranet.server.core;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.filter.AppListenFilter;
import top.ticho.intranet.server.repository.AppReposipory;
import top.ticho.intranet.server.repository.ClientRepository;

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
    ClientRepository clientRepository,
    AppReposipory appReposipory,
    AppListenFilter appListenFilter
) {

    public void start() {
        try {
            log.info("内网映射服务启动中，端口：{}，是否开启ssl：{}, ssl端口：{}", serverProperty.getPort(), serverProperty.getSslEnable(), serverProperty.getSslPort());
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
            log.error("内网映射服务启动失败");
            throw new RuntimeException(e);
        }
        serverStatus.set(ServerStatus.ENABLED.getCode());
        log.info("内网映射服务启动成功");
    }

    public boolean saveClient(String accessKey, String name) {
        return clientRepository.saveClient(accessKey, name);
    }

    /**
     * 根据accessKey删除客户端
     */
    public void deleteClient(String accessKey) {
        Optional<ClientInfo> clientInfoOpt = findClientByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        deleteClient(clientInfo);
    }

    /**
     * 删除所有客户端
     */
    public void deleteAllClient() {
        findAllClient().forEach(this::deleteClient);
    }

    public void deleteClient(ClientInfo clientInfo) {
        // 删除应用
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        Optional.ofNullable(portMap)
            .filter(MapUtil::isNotEmpty)
            .map(Map::keySet)
            .ifPresent(ports -> {
                ports.forEach(appReposipory::deleteApp);
                portMap.clear();
            });
        // 关闭请求通道
        clientRepository.closeRequestChannel(clientInfo);
        clientRepository.deleteClient(clientInfo.getAccessKey());
    }

    /**
     * 创建应用
     */
    public void createApp(PortInfo portInfo) {
        if (null == portInfo) {
            return;
        }
        Optional<ClientInfo> clientInfoOpt = findClientByAccessKey(portInfo.getAccessKey());
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        appReposipory.createApp(portInfo);
        portMap.put(portInfo.getPort(), portInfo);
    }

    /**
     * 刷新客户端的应用
     */
    public void flushApp(String accessKey, Map<Integer, PortInfo> portInfoMap) {
        if (MapUtil.isEmpty(portInfoMap)) {
            return;
        }
        Optional<ClientInfo> clientInfoOpt = findClientByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMapFromMem = clientInfo.getPortMap();
        // 如果客户端的端口MAP不为空，则删除内存中不应存在的端口
        portMapFromMem.values()
            .forEach(x -> {
                if (portInfoMap.containsKey(x.getPort())) {
                    return;
                }
                deleteApp(accessKey, x.getPort());
            });
    }

    /**
     * 根据accessKey删除应用
     */
    public void deleteApp(String accessKey) {
        if (StrUtil.isBlank(accessKey)) {
            return;
        }
        Optional<ClientInfo> clientInfoOpt = findClientByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty()) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        Map<Integer, PortInfo> portMap = clientInfo.getPortMap();
        portMap.keySet().removeIf(portNum -> {
            appReposipory.deleteApp(portNum);
            return true;
        });
    }

    /**
     * 根据accessKey和端口号删除应用
     */
    public void deleteApp(String accessKey, Integer portNum) {
        if (StrUtil.isBlank(accessKey) || Objects.isNull(portNum)) {
            return;
        }
        Optional<ClientInfo> clientInfoOpt = findClientByAccessKey(accessKey);
        if (clientInfoOpt.isEmpty() || MapUtil.isEmpty(clientInfoOpt.get().getPortMap())) {
            return;
        }
        ClientInfo clientInfo = clientInfoOpt.get();
        if (clientInfo.getPortMap().containsKey(portNum)) {
            PortInfo portInfo = clientInfo.getPortMap().get(portNum);
            appReposipory.deleteApp(portInfo.getPort());
            clientInfo.getPortMap().remove(portNum);
        }
    }

    public Optional<ClientInfo> findClientByAccessKey(String accessKey) {
        return clientRepository.findByAccessKey(accessKey);
    }

    private List<ClientInfo> findAllClient() {
        return clientRepository.findAll();
    }

}
