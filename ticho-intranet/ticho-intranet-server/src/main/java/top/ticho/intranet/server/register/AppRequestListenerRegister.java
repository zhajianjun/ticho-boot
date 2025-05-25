package top.ticho.intranet.server.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import top.ticho.intranet.server.core.ServerHandler;
import top.ticho.intranet.server.filter.AppListenProxyFilter;
import top.ticho.intranet.server.listener.AppRequestListener;

/**
 * App请求监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-18 15:11
 */
@AllArgsConstructor
public class AppRequestListenerRegister extends ChannelInitializer<SocketChannel> {

    private ServerHandler serverHandler;

    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addFirst(new AppListenProxyFilter().register(serverHandler.appListenFilter()));
        socketChannel.pipeline().addLast(new AppRequestListener(serverHandler));
    }

}
