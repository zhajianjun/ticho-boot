package top.ticho.intranet.server.handler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.common.ServerStatus;
import top.ticho.intranet.server.filter.AppListenFilter;
import top.ticho.intranet.server.repository.AppReposipory;
import top.ticho.intranet.server.repository.ClientRepository;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ServerCreateHandler {

    public static ServerContext buildServerContext(ServerProperty serverProperty, AppListenFilter appListenFilter) {
        NioEventLoopGroup serverBoss = new NioEventLoopGroup(serverProperty.getBossThreads());
        NioEventLoopGroup serverWorker = new NioEventLoopGroup(serverProperty.getWorkerThreads());
        AtomicInteger serverStatus = new AtomicInteger(ServerStatus.STARTING.getCode());
        return new ServerContext(
            serverStatus,
            serverBoss,
            serverWorker,
            serverProperty,
            new ClientRepository(),
            new AppReposipory(serverProperty),
            appListenFilter
        );
    }

    public static ServerBootstrap createServerBootstrap(ServerContext serverContext) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ServerBootstrap group = serverBootstrap.group(serverContext.serverBoss(), serverContext.serverWorker());
        ServerBootstrap channel = group.channel(NioServerSocketChannel.class);
        ServerListenHandlerRegister childHandler = new ServerListenHandlerRegister(serverContext);
        channel.childHandler(childHandler);
        return serverBootstrap;
    }

    public static ServerBootstrap createSslServerBootstrap(ServerContext serverContext) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        ServerBootstrap group = serverBootstrap.group(serverContext.serverBoss(), serverContext.serverWorker());
        ServerBootstrap channel = group.channel(NioServerSocketChannel.class);
        SslServerListenHandlerRegister childHandler = new SslServerListenHandlerRegister(serverContext);
        channel.childHandler(childHandler);
        return serverBootstrap;
    }

    public static void creteAppBootstrap(ServerContext serverContext) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        AppReposipory appReposipory = serverContext.appReposipory();
        appReposipory.addServerBootstrap(serverBootstrap);
        ServerBootstrap group = serverBootstrap.group(serverContext.serverBoss(), serverContext.serverWorker());
        ServerBootstrap channel = group.channel(NioServerSocketChannel.class);
        AppListenHandlerRegister childHandler = new AppListenHandlerRegister(serverContext);
        channel.childHandler(childHandler);
    }

}
