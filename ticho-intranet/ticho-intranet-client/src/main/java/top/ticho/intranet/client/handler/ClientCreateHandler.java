package top.ticho.intranet.client.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ClientRepository;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * @author zhajianjun
 * @date 2025-05-19 23:15
 */
public class ClientCreateHandler {


    public static ClientContext init(ClientProperty clientProperty) {
        ClientContext clientContext = buildClientContext(clientProperty);
        ClientCreateHandler.createClientBootstrap(clientContext);
        ClientCreateHandler.createAppBootstrap(clientContext);
        return clientContext;
    }

    public static ClientContext buildClientContext(ClientProperty clientProperty) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(clientProperty.getWorkerThreads());
        return new ClientContext(
            workerGroup,
            clientProperty,
            new ClientRepository(clientProperty),
            new AppReposipory()
        );
    }

    public static void createClientBootstrap(ClientContext clientContext) {
        ClientRepository clientRepository = clientContext.clientRepository();
        Bootstrap bootstrap = new Bootstrap();
        clientRepository.addBootstrap(bootstrap);
        bootstrap.group(clientContext.workerGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ClientListenHandlerRegister(clientContext));
    }

    public static void createAppBootstrap(ClientContext clientContext) {
        AppReposipory appReposipory = clientContext.appReposipory();
        Bootstrap bootstrap = new Bootstrap();
        appReposipory.addBootstrap(bootstrap);
        bootstrap.group(clientContext.workerGroup());
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new AppListenHandlerRegister(appReposipory));
    }

}
