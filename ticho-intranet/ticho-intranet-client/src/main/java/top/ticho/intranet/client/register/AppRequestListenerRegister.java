package top.ticho.intranet.client.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.client.listener.AppRequestListener;

/**
 * App请求监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-19 23:13
 */
public class AppRequestListenerRegister extends ChannelInitializer<SocketChannel> {

    private final IntranetClientHandler intranetClientHandler;

    public AppRequestListenerRegister(IntranetClientHandler intranetClientHandler) {
        this.intranetClientHandler = intranetClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new AppRequestListener(intranetClientHandler));
    }

}