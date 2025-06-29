package top.ticho.intranet.client.core;

import io.netty.channel.Channel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import top.ticho.intranet.client.support.IntranetApplicationSupport;
import top.ticho.intranet.client.support.IntranetClientSupport;
import top.ticho.intranet.common.prop.IntranetClientProperty;

/**
 * 客户端处理器
 *
 * @author zhajianjun
 * @date 2025-05-20 22:48
 */
public record IntranetClientHandler(
    NioEventLoopGroup workerGroup,
    IntranetClientProperty intranetClientProperty,
    IntranetClientSupport intranetClientSupport,
    IntranetApplicationSupport intranetApplicationSupport
) {
    public void start() {
        intranetClientSupport.start();
    }

    public void restart() {
        intranetClientSupport.restart();
    }

    public void stop(int status) {
        workerGroup.shutdownGracefully();
        intranetApplicationSupport.clearRequestChannels();
        System.exit(status);
    }

    public void connectServer(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        intranetClientSupport.connect(host, port, listener);
    }

    public void setServerChannel(Channel channel) {
        intranetClientSupport.setServerChannel(channel);
    }

    public Channel getServerChannel() {
        return intranetClientSupport.getServerChannel();
    }


    public void saveReadyServerChannel(Channel channel) {
        intranetClientSupport.saveReadyServerChannel(channel);
    }

    public void removeReadyServerChannel(Channel channel) {
        intranetClientSupport.removeReadyServerChannel(channel);
    }

    public Channel getReadyServerChannel() {
        return intranetClientSupport.getReadyServerChannel();
    }

    public void request(String host, Integer port, GenericFutureListener<? extends Future<? super Void>> listener) {
        intranetApplicationSupport.connect(host, port, listener);
    }

    public void saveRequestChannel(String requestId, Channel channel) {
        intranetApplicationSupport.saveRequestChannel(requestId, channel);
    }

    public void clearRequestChannels() {
        intranetApplicationSupport.clearRequestChannels();
    }

    public void removeRequestChannel(String requestId) {
        intranetApplicationSupport.removeRequestChannel(requestId);
    }


}
