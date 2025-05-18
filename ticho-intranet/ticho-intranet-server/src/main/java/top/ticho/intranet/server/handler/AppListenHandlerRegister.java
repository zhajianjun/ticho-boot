package top.ticho.intranet.server.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import top.ticho.intranet.server.filter.AppListenProxyFilter;

/**
 * @author zhajianjun
 * @date 2025-05-18 15:11
 */
@AllArgsConstructor
public class AppListenHandlerRegister extends ChannelInitializer<SocketChannel> {

    private ServerContext serverContext;

    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addFirst(new AppListenProxyFilter().register(serverContext.appListenFilter()));
        socketChannel.pipeline().addLast(new AppListenHandler(serverContext));
    }

}
