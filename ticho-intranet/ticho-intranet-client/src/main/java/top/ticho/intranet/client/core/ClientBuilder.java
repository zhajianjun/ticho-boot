package top.ticho.intranet.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.ticho.intranet.client.register.AppRequestListenerRegister;
import top.ticho.intranet.client.register.ServerMessageListenerRegister;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ClientRepository;
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
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerGroup); // 设置事件循环组
        clientBootstrap.channel(NioSocketChannel.class); // 设置通道类型为NIO Socket通道

        // 创建应用的Bootstrap对象，配置类似于客户端Bootstrap
        Bootstrap appBootstrap = new Bootstrap();
        appBootstrap.group(workerGroup);
        appBootstrap.channel(NioSocketChannel.class);

        // 创建客户端仓库对象，用于管理客户端的相关信息和配置
        ClientRepository clientRepository = new ClientRepository(clientProperty, clientBootstrap);

        // 创建应用仓库对象，用于管理应用的相关信息和配置
        AppReposipory appReposipory = new AppReposipory(appBootstrap);

        // 创建客户端上下文对象，将上述配置和仓库对象传入
        ClientHandler clientHandler = new ClientHandler(workerGroup, clientProperty, clientRepository, appReposipory);

        // 为客户端和应用的Bootstrap对象设置初始化处理器
        clientBootstrap.handler(new ServerMessageListenerRegister(clientHandler));
        appBootstrap.handler(new AppRequestListenerRegister(clientHandler));

        // 返回初始化后的客户端上下文对象
        return clientHandler;
    }

}
