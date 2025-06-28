package top.ticho.intranet.server.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import lombok.AllArgsConstructor;
import top.ticho.intranet.server.core.IntranetServerHandler;
import top.ticho.intranet.server.filter.IntranetApplicationListenProxyFilter;
import top.ticho.intranet.server.listener.AppRequestListener;

/**
 * App请求监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-18 15:11
 */
@AllArgsConstructor
public class AppRequestListenerRegister extends ChannelInitializer<SocketChannel> {

    private IntranetServerHandler intranetServerHandler;

    protected void initChannel(SocketChannel socketChannel) {
        socketChannel.pipeline().addFirst(new IntranetApplicationListenProxyFilter().register(intranetServerHandler.intranetApplicationListenFilter()));
        socketChannel.pipeline().addLast(new AppRequestListener(intranetServerHandler));
    }

}
