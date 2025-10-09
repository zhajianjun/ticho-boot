package top.ticho.intranet.client.register;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.client.listener.ServerMessageListener;
import top.ticho.intranet.common.constant.TiIntranetConst;
import top.ticho.intranet.common.core.IdleChecker;
import top.ticho.intranet.common.core.MessageDecoder;
import top.ticho.intranet.common.core.MessageEncoder;
import top.ticho.intranet.common.core.SslHandler;
import top.ticho.intranet.common.prop.IntranetClientProperty;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * 服务端消息监听器注册器
 *
 * @author zhajianjun
 * @date 2025-05-20 23:10
 */
public class ServerMessageListenerRegister extends ChannelInitializer<SocketChannel> {

    private final IntranetClientHandler intranetClientHandler;

    public ServerMessageListenerRegister(IntranetClientHandler intranetClientHandler) {
        this.intranetClientHandler = intranetClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) {
        IntranetClientProperty intranetClientProperty = intranetClientHandler.intranetClientProperty();
        if (Boolean.TRUE.equals(intranetClientProperty.getSslEnable())) {
            SslHandler sslHandler = new SslHandler(intranetClientProperty.getSslPath(), intranetClientProperty.getSslPassword());
            SSLContext sslContext = sslHandler.getSslContext();
            SSLEngine engine = sslContext.createSSLEngine();
            engine.setUseClientMode(true);
            socketChannel.pipeline().addLast(new io.netty.handler.ssl.SslHandler(engine));
        }
        socketChannel.pipeline().addLast(new MessageDecoder(TiIntranetConst.MAX_FRAME_LEN, TiIntranetConst.FIELD_OFFSET, TiIntranetConst.FIELD_LEN, TiIntranetConst.ADJUSTMENT, TiIntranetConst.INIT_BYTES_TO_STRIP));
        socketChannel.pipeline().addLast(new MessageEncoder());
        socketChannel.pipeline().addLast(new IdleChecker(TiIntranetConst.READ_IDLE_TIME, TiIntranetConst.WRITE_IDLE_TIME - 10, 0));
        socketChannel.pipeline().addLast(new ServerMessageListener(intranetClientHandler));
    }

}