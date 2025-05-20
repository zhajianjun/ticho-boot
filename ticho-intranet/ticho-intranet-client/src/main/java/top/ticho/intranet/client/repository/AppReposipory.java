package top.ticho.intranet.client.repository;

import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhajianjun
 * @date 2025-05-19 23:12
 */
public class AppReposipory {
    /** 监听app */
    private static final Map<String, Channel> requestChannelMap = new ConcurrentHashMap<>();
    /** 监听客户端，用于监听服务器想要请求的应用地址 */
    private Bootstrap bootstrap;

    public void addBootstrap(Bootstrap bootstrap) {
        this.bootstrap = bootstrap;
    }

    public void connect(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        bootstrap.connect(host, port).addListener(listener);
    }

    public void saveRequestChannel(String uri, Channel channel) {
        requestChannelMap.put(uri, channel);
    }

    public void removeRequestChannel(String requestId) {
        if (StrUtil.isEmpty(requestId)) {
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
