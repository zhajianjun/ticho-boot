package top.ticho.intranet.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.ticho.intranet.client.register.AppRequestListenerRegister;
import top.ticho.intranet.client.register.ServerMessageListenerRegister;
import top.ticho.intranet.client.support.ApplicationSupport;
import top.ticho.intranet.client.support.ClientSupport;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * 客户端构建器
 *
 * @author zhajianjun
 * @date 2025-05-19 23:15
 */
public class ClientBuilder {

    /**
     * 初始化客户端上下文
     *
     * @param clientProperty 客户端属性配置，包含一些客户端初始化参数
     * @return 返回初始化后的客户端上下文对象
     */
    public static ClientHandler build(ClientProperty clientProperty) {
        // 创建一个NIO事件循环组，用于处理I/O操作
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(clientProperty.getWorkerThreads());
        // 创建客户端的Bootstrap对象，用于配置客户端的NIO通道
        Bootstrap clientBootstrap = createBootstrap(workerGroup);
        // 创建应用的Bootstrap对象，配置类似于客户端Bootstrap
        Bootstrap appBootstrap = createBootstrap(workerGroup);
        // 创建客户端仓库对象，用于管理客户端的相关信息和配置
        ClientSupport clientSupport = new ClientSupport(clientProperty, clientBootstrap);
        // 创建应用仓库对象，用于管理应用的相关信息和配置
        ApplicationSupport applicationSupport = new ApplicationSupport(appBootstrap);
        // 创建客户端上下文对象，将上述配置和仓库对象传入
        ClientHandler clientHandler = new ClientHandler(workerGroup, clientProperty, clientSupport, applicationSupport);
        // 为客户端和应用的Bootstrap对象设置初始化处理器
        clientBootstrap.handler(new ServerMessageListenerRegister(clientHandler));
        appBootstrap.handler(new AppRequestListenerRegister(clientHandler));
        // 返回初始化后的客户端上下文对象
        return clientHandler;
    }

    private static Bootstrap createBootstrap(NioEventLoopGroup workerGroup) {
        // 创建客户端的Bootstrap对象，用于配置客户端的NIO通道
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup); // 添加事件循环组
        bootstrap.channel(NioSocketChannel.class); // 设置通道类型为NIO Socket通道
        return bootstrap;
    }

}
