package top.ticho.intranet.server.core;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.filter.AppListenFilter;
import top.ticho.intranet.server.filter.DefaultAppListenFilter;
import top.ticho.intranet.server.register.AppRequestListenerRegister;
import top.ticho.intranet.server.register.ClientMessageListenerRegister;
import top.ticho.intranet.server.register.SslClientMessageListenerRegister;
import top.ticho.intranet.server.repository.AppReposipory;
import top.ticho.intranet.server.repository.ClientRepository;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 服务端构建器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ServerBuilder {

    public static ServerHandler init(ServerProperty serverProperty) {
        ServerHandler serverHandler = build(serverProperty);
        serverHandler.init();
        return serverHandler;
    }

    public static ServerHandler init(ServerProperty serverProperty, AppListenFilter appListenFilter) {
        ServerHandler serverHandler = build(serverProperty, appListenFilter);
        serverHandler.init();
        return serverHandler;
    }

    public static ServerHandler build(ServerProperty serverProperty) {
        return build(serverProperty, new DefaultAppListenFilter());
    }

    public static ServerHandler build(ServerProperty serverProperty, AppListenFilter appListenFilter) {
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
        ServerHandler serverHandler = new ServerHandler(
            serverStatus,
            serverBoss,
            serverWorker,
            serverProperty,
            serverBootstrap,
            sslServerBootstrap,
            new ClientRepository(),
            new AppReposipory(serverProperty, appServerBootstrap),
            appListenFilter
        );
        serverBootstrap.childHandler(new ClientMessageListenerRegister(serverHandler));
        if (sslEnable) {
            sslServerBootstrap.childHandler(new SslClientMessageListenerRegister(serverHandler));
        }
        appServerBootstrap.childHandler(new AppRequestListenerRegister(serverHandler));
        return serverHandler;
    }

    private static ServerBootstrap createBootstrap(NioEventLoopGroup serverBoss, NioEventLoopGroup serverWorker) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(serverBoss, serverWorker);
        serverBootstrap.channel(NioServerSocketChannel.class);
        return serverBootstrap;
    }

}
