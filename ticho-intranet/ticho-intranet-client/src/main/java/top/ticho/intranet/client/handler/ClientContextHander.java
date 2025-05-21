package top.ticho.intranet.client.handler;

import top.ticho.intranet.client.repository.ServerRepository;
import top.ticho.intranet.common.prop.ClientProperty;

/**
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientContextHander {

    private final ServerRepository serverRepository;

    public ClientContextHander(ClientProperty clientProperty) {
        ClientContext clientContext = CreateHandler.buildClientContext(clientProperty);
        this.serverRepository = clientContext.serverRepository();
        CreateHandler.createClientBootstrap(clientContext);
        CreateHandler.createAppBootstrap(clientContext);
    }

    public void start() {
        serverRepository.start();
    }

}
