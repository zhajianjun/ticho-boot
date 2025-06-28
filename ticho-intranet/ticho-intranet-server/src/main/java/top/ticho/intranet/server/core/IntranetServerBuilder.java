package top.ticho.intranet.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.ticho.intranet.common.prop.ServerProperty;
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

    public static IntranetServerHandler init(ServerProperty serverProperty) {
        IntranetServerHandler intranetServerHandler = build(serverProperty);
        intranetServerHandler.init();
        return intranetServerHandler;
    }

    public static IntranetServerHandler init(ServerProperty serverProperty, IntranetApplicationListenFilter intranetApplicationListenFilter) {
        IntranetServerHandler intranetServerHandler = build(serverProperty, intranetApplicationListenFilter);
        intranetServerHandler.init();
        return intranetServerHandler;
    }

    public static IntranetServerHandler build(ServerProperty serverProperty) {
        return build(serverProperty, new DefaultIntranetApplicationListenFilter());
    }

    public static IntranetServerHandler build(ServerProperty serverProperty, IntranetApplicationListenFilter intranetApplicationListenFilter) {
        NioEventLoopGroup serverBoss = new NioEventLoopGroup(serverProperty.getBossThreads());
        NioEventLoopGroup serverWorker = new NioEventLoopGroup(serverProperty.getWorkerThreads());
        boolean sslEnable = Boolean.TRUE.equals(serverProperty.getSslEnable());
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
            serverProperty,
            serverBootstrap,
            sslServerBootstrap,
            new IntranetClientSupport(),
            new IntranetApplicationSupport(serverProperty, appServerBootstrap),
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
