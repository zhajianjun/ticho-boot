package top.ticho.intranet.server.register;

import io.netty.channel.socket.SocketChannel;
import top.ticho.intranet.common.constant.CommConst;
import top.ticho.intranet.common.core.SslHandler;
import top.ticho.intranet.common.prop.IntranetServerProperty;
import top.ticho.intranet.server.core.IntranetServerHandler;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * @author zhajianjun
 * @date 2025-05-18 15:37
 */
public class SslClientMessageListenerRegister extends ClientMessageListenerRegister {

    /** ssl证书路径 */
    private final String sslPath;
    /** ssl证书密码 */
    private final String sslPassword;

    public SslClientMessageListenerRegister(IntranetServerHandler intranetServerHandler) {
        super(intranetServerHandler);
        IntranetServerProperty intranetServerProperty = intranetServerHandler.intranetServerProperty();
        this.sslPath = intranetServerProperty.getSslPath();
        this.sslPassword = intranetServerProperty.getSslPassword();
    }

    protected void initChannel(SocketChannel sc) {
        SslHandler sslHandler = new SslHandler(sslPath, sslPassword);
        SSLContext sslContext = sslHandler.getSslContext();
        SSLEngine engine = sslContext.createSSLEngine();
        engine.setUseClientMode(false);
        engine.setNeedClientAuth(true);
        sc.pipeline().addLast(CommConst.SSL, new io.netty.handler.ssl.SslHandler(engine));
        super.initChannel(sc);
    }

}
