package top.ticho.intranet.client.core;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.ticho.intranet.client.register.AppRequestListenerRegister;
import top.ticho.intranet.client.register.ServerMessageListenerRegister;
import top.ticho.intranet.client.support.IntranetApplicationSupport;
import top.ticho.intranet.client.support.IntranetClientSupport;
import top.ticho.intranet.common.prop.IntranetClientProperty;

/**
 * 客户端构建器
 *
 * @author zhajianjun
 * @date 2025-05-19 23:15
 */
public class IntranetClientBuilder {

    /**
     * 初始化客户端上下文
     *
     * @param intranetClientProperty 客户端属性配置，包含一些客户端初始化参数
     * @return 返回初始化后的客户端上下文对象
     */
    public static IntranetClientHandler build(IntranetClientProperty intranetClientProperty) {
        // 创建一个NIO事件循环组，用于处理I/O操作
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(intranetClientProperty.getWorkerThreads());
        // 创建客户端的Bootstrap对象，用于配置客户端的NIO通道
        Bootstrap clientBootstrap = createBootstrap(workerGroup);
        // 创建应用的Bootstrap对象，配置类似于客户端Bootstrap
        Bootstrap appBootstrap = createBootstrap(workerGroup);
        // 创建客户端仓库对象，用于管理客户端的相关信息和配置
        IntranetClientSupport intranetClientSupport = new IntranetClientSupport(intranetClientProperty, clientBootstrap);
        // 创建应用仓库对象，用于管理应用的相关信息和配置
        IntranetApplicationSupport intranetApplicationSupport = new IntranetApplicationSupport(appBootstrap);
        // 创建客户端上下文对象，将上述配置和仓库对象传入
        IntranetClientHandler intranetClientHandler = new IntranetClientHandler(workerGroup, intranetClientProperty, intranetClientSupport, intranetApplicationSupport);
        // 为客户端和应用的Bootstrap对象设置初始化处理器
        clientBootstrap.handler(new ServerMessageListenerRegister(intranetClientHandler));
        appBootstrap.handler(new AppRequestListenerRegister(intranetClientHandler));
        // 返回初始化后的客户端上下文对象
        return intranetClientHandler;
    }

    private static Bootstrap createBootstrap(NioEventLoopGroup workerGroup) {
        // 创建客户端的Bootstrap对象，用于配置客户端的NIO通道
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup); // 添加事件循环组
        bootstrap.channel(NioSocketChannel.class); // 设置通道类型为NIO Socket通道
        return bootstrap;
    }

}
