package top.ticho.intranet.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.ticho.intranet.common.prop.IntranetServerProperty;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.filter.IntranetApplicationListenFilter;
import top.ticho.intranet.server.filter.DefaultIntranetApplicationListenFilter;
import top.ticho.intranet.server.register.AppRequestListenerRegister;
import top.ticho.intranet.server.register.ClientMessageListenerRegister;
import top.ticho.intranet.server.register.SslClientMessageListenerRegister;
import top.ticho.intranet.server.support.IntranetApplicationSupport;
import top.ticho.intranet.server.support.IntranetClientSupport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端构建器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class IntranetServerBuilder {

    public static IntranetServerHandler init(IntranetServerProperty intranetServerProperty) {
        IntranetServerHandler intranetServerHandler = build(intranetServerProperty);
        intranetServerHandler.init();
        return intranetServerHandler;
    }

    public static IntranetServerHandler init(IntranetServerProperty intranetServerProperty, IntranetApplicationListenFilter intranetApplicationListenFilter) {
        IntranetServerHandler intranetServerHandler = build(intranetServerProperty, intranetApplicationListenFilter);
        intranetServerHandler.init();
        return intranetServerHandler;
    }

    public static IntranetServerHandler build(IntranetServerProperty intranetServerProperty) {
        return build(intranetServerProperty, new DefaultIntranetApplicationListenFilter());
    }

    public static IntranetServerHandler build(IntranetServerProperty intranetServerProperty, IntranetApplicationListenFilter intranetApplicationListenFilter) {
        NioEventLoopGroup serverBoss = new NioEventLoopGroup(intranetServerProperty.getBossThreads());
        NioEventLoopGroup serverWorker = new NioEventLoopGroup(intranetServerProperty.getWorkerThreads());
        boolean sslEnable = Boolean.TRUE.equals(intranetServerProperty.getSslEnable());
        AtomicInteger serverStatus = new AtomicInteger(ServerStatus.INITING.getCode());
        ServerBootstrap serverBootstrap = createBootstrap(serverBoss, serverWorker);
        ServerBootstrap sslServerBootstrap = null;
        if (sslEnable) {
            sslServerBootstrap = createBootstrap(serverBoss, serverWorker);
        }
        ServerBootstrap appServerBootstrap = createBootstrap(serverBoss, serverWorker);
        IntranetServerHandler intranetServerHandler = new IntranetServerHandler(
            serverStatus,
            serverBoss,
            serverWorker,
            intranetServerProperty,
            serverBootstrap,
            sslServerBootstrap,
            new IntranetClientSupport(),
            new IntranetApplicationSupport(intranetServerProperty, appServerBootstrap),
            intranetApplicationListenFilter
        );
        serverBootstrap.childHandler(new ClientMessageListenerRegister(intranetServerHandler));
        if (sslEnable) {
            sslServerBootstrap.childHandler(new SslClientMessageListenerRegister(intranetServerHandler));
        }
        appServerBootstrap.childHandler(new AppRequestListenerRegister(intranetServerHandler));
        return intranetServerHandler;
    }

    private static ServerBootstrap createBootstrap(NioEventLoopGroup serverBoss, NioEventLoopGroup serverWorker) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(serverBoss, serverWorker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        return serverBootstrap;
    }

}
