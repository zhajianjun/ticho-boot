package top.ticho.intranet.client.handler;

import top.ticho.intranet.client.repository.AppReposipory;
import top.ticho.intranet.client.repository.ServerRepository;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientContextHander {

    private final ClientContext clientContext;
    private final ServerRepository serverRepository;
    private final AppReposipory appReposipory;

    public ClientContextHander(ClientProperty clientProperty) {
        this.clientContext = CreateHandler.buildClientContext(clientProperty);
        this.serverRepository = clientContext.serverRepository();
        this.appReposipory = clientContext.appReposipory();
        CreateHandler.createClientBootstrap(clientContext);
        CreateHandler.createAppBootstrap(clientContext);
    }

    public void start() {
        serverRepository.start();
    }

    public void restart() {
        serverRepository.restart();
    }

    public void stop(byte status) {
        clientContext.workerGroup().shutdownGracefully();
        appReposipory.clearRequestChannels();
        System.exit(status);
    }

}
