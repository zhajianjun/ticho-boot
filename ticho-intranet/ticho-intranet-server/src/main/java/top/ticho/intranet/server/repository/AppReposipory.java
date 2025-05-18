package top.ticho.intranet.server.repository;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;
import top.ticho.intranet.server.entity.PortInfo;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zhajianjun
 * @date 2025-05-18 11:53
 */
@Slf4j
public class AppReposipory {
    /** 与绑定端口的通道 */
    private final Map<Integer, Channel> bindPortChannelMap;
    /** 请求id */
    private final AtomicLong requestId;
    /** 配置 */
    private final ServerProperty serverProperty;
    private ServerBootstrap serverBootstrap;

    public AppReposipory(ServerProperty serverProperty) {
        this.serverProperty = serverProperty;
        this.requestId = new AtomicLong(0L);
        this.bindPortChannelMap = new ConcurrentHashMap<>();
    }

    public void addServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public boolean exists(Integer portNum) {
        if (Objects.isNull(portNum)) {
            return false;
        }
        return bindPortChannelMap.containsKey(portNum);
    }

    public void createApp(PortInfo portInfo) {
        Integer port;
        if (Objects.isNull(portInfo) || Objects.isNull(port = portInfo.getPort())) {
            return;
        }
        if (bindPortChannelMap.containsKey(port)) {
            log.warn("创建应用失败，端口：{}已被创建", port);
            return;
        }
        Long maxBindPorts = serverProperty.getMaxBindPorts();
        if (bindPortChannelMap.size() >= maxBindPorts) {
            log.warn("创建应用失败，端口：{} 超出最大绑定端口数{}", port, maxBindPorts);
        }
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port);
            channelFuture.get();
            bindPortChannelMap.put(port, channelFuture.channel());
        } catch (InterruptedException | ExecutionException e) {
            log.error("创建应用失败，端口：{}，错误信息：{}", port, e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
        log.info("创建应用成功，端口：{}", port);
    }

    public void deleteApp(Integer port) {
        if (null == port) {
            return;
        }
        Channel channel = bindPortChannelMap.get(port);
        if (channel == null) {
            return;
        }
        IntranetUtil.close(channel);
        bindPortChannelMap.remove(port);
        log.info("删除应用成功，端口：{}", port);
    }

    public String getRequestId() {
        return String.valueOf(requestId.incrementAndGet());
    }

    public int size() {
        return bindPortChannelMap.size();
    }

}
