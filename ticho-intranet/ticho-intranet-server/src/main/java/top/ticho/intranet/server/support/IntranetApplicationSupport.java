package top.ticho.intranet.server.support;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.exception.IntranetException;
import top.ticho.intranet.common.prop.IntranetServerProperty;
import top.ticho.intranet.common.util.IntranetUtil;

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
public class IntranetApplicationSupport {
    /** 与绑定端口的通道 */
    private final Map<Integer, Channel> bindPortChannelMap;
    /** 请求id */
    private final AtomicLong requestId;
    /** 配置 */
    private final IntranetServerProperty intranetServerProperty;
    private final ServerBootstrap appServerBootstrap;

    public IntranetApplicationSupport(IntranetServerProperty intranetServerProperty, ServerBootstrap appServerBootstrap) {
        this.intranetServerProperty = intranetServerProperty;
        this.appServerBootstrap = appServerBootstrap;
        this.requestId = new AtomicLong(0L);
        this.bindPortChannelMap = new ConcurrentHashMap<>();
    }

    public boolean exists(Integer portNum) {
        if (Objects.isNull(portNum)) {
            return false;
        }
        return bindPortChannelMap.containsKey(portNum);
    }

    public boolean bind(Integer port) {
        if (Objects.isNull(port)) {
            return false;
        }
        if (port > CommConst.MAX_PORT) {
            log.warn("绑定应用失败，端口：{}, 可用的端口号范围是从{}到{}", port, CommConst.MIN_PORT, CommConst.MAX_PORT);
            return false;
        }
        if (bindPortChannelMap.containsKey(port)) {
            log.warn("绑定应用失败，端口：{}已被绑定", port);
            return false;
        }
        Long maxBindPorts = intranetServerProperty.getMaxBindPorts();
        if (bindPortChannelMap.size() >= maxBindPorts) {
            log.warn("绑定应用失败，端口：{} 超出最大绑定端口数{}", port, maxBindPorts);
            return false;
        }
        try {
            ChannelFuture channelFuture = appServerBootstrap.bind(port);
            channelFuture.get();
            bindPortChannelMap.put(port, channelFuture.channel());
        } catch (InterruptedException | ExecutionException e) {
            log.error("绑定应用失败，端口：{}，错误信息：{}", port, e.getMessage(), e);
            throw new IntranetException(e.getMessage(), e);
        }
        log.info("绑定应用成功，端口：{}", port);
        return true;
    }

    public boolean unbind(Integer port) {
        if (null == port) {
            return false;
        }
        Channel channel = bindPortChannelMap.get(port);
        if (channel == null) {
            log.warn("解绑应用失败，端口：{}不存在", port);
            return false;
        }
        IntranetUtil.close(channel);
        bindPortChannelMap.remove(port);
        log.info("解绑应用成功，端口：{}", port);
        return true;
    }

    public String getRequestId() {
        return String.valueOf(requestId.incrementAndGet());
    }

    public int size() {
        return bindPortChannelMap.size();
    }

}
