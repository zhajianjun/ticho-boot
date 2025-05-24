package top.ticho.intranet.client.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.client.listener.AppRequestListener;

/**
 * App请求监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-19 23:13
 */
public class AppRequestListenerRegister extends ChannelInitializer<SocketChannel> {

    private final ClientHandler clientHandler;

    public AppRequestListenerRegister(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    @Override
    protected void initChannel(SocketChannel channel) {
        channel.pipeline().addLast(new AppRequestListener(clientHandler));
    }

}