package top.ticho.intranet.client.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.common.core.SslHandler;
import top.ticho.intranet.common.prop.ClientProperty;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author zhajianjun
 * @date 2025-05-20 23:10
 */
public class ClientListenHandlerRegister extends ChannelInitializer<SocketChannel> {

    private final ClientContext clientContext;

    public ClientListenHandlerRegister(ClientContext clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        ClientProperty clientProperty = clientContext.clientProperty();
        if (Boolean.TRUE.equals(clientProperty.getSslEnable())) {
            SslHandler sslHandler = new SslHandler(clientProperty.getSslPath(), clientProperty.getSslPassword());
            SSLContext sslContext = sslHandler.getSslContext();
            SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(true);
            socketChannel.pipeline().addLast(new io.netty.handler.ssl.SslHandler(engine));
        }
        socketChannel.pipeline().addLast(new MessageDecoder(CommConst.MAX_FRAME_LEN, CommConst.FIELD_OFFSET, CommConst.FIELD_LEN, CommConst.ADJUSTMENT, CommConst.INIT_BYTES_TO_STRIP));
        socketChannel.pipeline().addLast(new MessageEncoder());
        socketChannel.pipeline().addLast(new IdleChecker(CommConst.READ_IDLE_TIME, CommConst.WRITE_IDLE_TIME - 10, 0));
        socketChannel.pipeline().addLast(new ClientListenHandler(clientContext));
    }

}