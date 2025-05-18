package top.ticho.intranet.server;

import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.handler.ServerContextHandler;

/**
 * 服务端启动测试
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class ServerStartTest {

    public static void main(String[] args) {
        // 测试
        Configurator.initialize("Server", "log4j2.xml");
        ServerProperty serverProperty = new ServerProperty();
        serverProperty.setPort(5120);
        serverProperty.setSslEnable(false);
        ServerContextHandler serverContextHandler = new ServerContextHandler(serverProperty);
        String accessKey = "68bfe8f0af124ecfa093350ab8d4b44f";
        serverContextHandler.saveClient(accessKey, "test");
        PortInfo portInfo = PortInfo.builder()
            .accessKey(accessKey)
            .port(80)
            .endpoint("127.0.0.1:5122")
            .build();
        serverContextHandler.createApp(portInfo);
    }

}
