package top.ticho.intranet.client.core;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.intranet.client.support.ApplicationSupport;
import top.ticho.intranet.client.support.ClientSupport;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * 客户端处理器
 *
 * @author zhajianjun
 * @date 2025-05-20 22:48
 */
public record ClientHandler(
    NioEventLoopGroup workerGroup,
    ClientProperty clientProperty,
    ClientSupport clientSupport,
    ApplicationSupport applicationSupport
) {
    public void start() {
        clientSupport.start();
    }

    public void restart() {
        clientSupport.restart();
    }

    public void stop(int status) {
        workerGroup.shutdownGracefully();
        applicationSupport.clearRequestChannels();
        System.exit(status);
    }

    public void connectServer(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        clientSupport.connect(host, port, listener);
    }

    public void setServerChannel(Channel channel) {
        clientSupport.setServerChannel(channel);
    }

    public Channel getServerChannel() {
        return clientSupport.getServerChannel();
    }


    public void saveReadyServerChannel(Channel channel) {
        clientSupport.saveReadyServerChannel(channel);
    }

    public void removeReadyServerChannel(Channel channel) {
        clientSupport.removeReadyServerChannel(channel);
    }

    public Channel getReadyServerChannel() {
        return clientSupport.getReadyServerChannel();
    }

    public void request(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        applicationSupport.connect(host, port, listener);
    }

    public void saveRequestChannel(String requestId, Channel channel) {
        applicationSupport.saveRequestChannel(requestId, channel);
    }

    public void clearRequestChannels() {
        applicationSupport.clearRequestChannels();
    }

    public void removeRequestChannel(String requestId) {
        applicationSupport.removeRequestChannel(requestId);
    }


}
