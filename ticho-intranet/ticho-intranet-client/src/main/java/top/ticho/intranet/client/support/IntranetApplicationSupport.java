package top.ticho.intranet.client.support;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.tool.core.TiStrUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2025-05-19 23:12
 */
public class IntranetApplicationSupport {
    /** 监听app */
    private final Map<String, Channel> requestChannelMap = new ConcurrentHashMap<>();
    /** 监听客户端，用于监听服务器想要请求的应用地址 */
    private final Bootstrap appBootstrap;

    public IntranetApplicationSupport(Bootstrap appBootstrap) {
        this.appBootstrap = appBootstrap;
    }

    public void connect(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        appBootstrap.connect(host, port).addListener(listener);
    }

    public void saveRequestChannel(String requestId, Channel channel) {
        requestChannelMap.put(requestId, channel);
    }

    public void removeRequestChannel(String requestId) {
        if (TiStrUtil.isEmpty(requestId)) {
            return;
        }
        requestChannelMap.remove(requestId);
    }

    public void clearRequestChannels() {
        for (Map.Entry<String, Channel> entry : requestChannelMap.entrySet()) {
            Channel channel = entry.getValue();
            if (channel.isActive()) {
                channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
            }
        }
        requestChannelMap.clear();
    }

}
