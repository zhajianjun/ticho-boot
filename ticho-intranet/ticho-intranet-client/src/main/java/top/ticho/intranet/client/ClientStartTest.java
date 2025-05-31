package top.ticho.intranet.client;

import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.client.core.ClientBuilder;
import top.ticho.intranet.client.core.ClientHandler;
import top.ticho.intranet.common.prop.ClientProperty;


/**
 * 客户端启动测试
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ClientStartTest {

    public static void main(String[] args) {
        Configurator.initialize("Client", "config/log4j2.xml");
        ClientProperty clientProperty = new ClientProperty();
        clientProperty.setAccessKey("68bfe8f0af124ecfa093350ab8d4b44f");
        clientProperty.setServerHost("127.0.0.1");
        clientProperty.setServerPort(5120);
        clientProperty.setSslEnable(false);
        ClientHandler clientHandler = ClientBuilder.build(clientProperty);
        clientHandler.start();
    }

}