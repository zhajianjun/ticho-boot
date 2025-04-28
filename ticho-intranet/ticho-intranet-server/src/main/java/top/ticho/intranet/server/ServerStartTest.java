package top.ticho.intranet.server;

import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.common.prop.ServerProperty;
import top.ticho.intranet.server.entity.ClientInfo;
import top.ticho.intranet.server.entity.PortInfo;
import top.ticho.intranet.server.handler.ServerHandler;

import java.util.HashMap;
import java.util.Map;

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
        ServerHandler serverHandler = new ServerHandler(serverProperty);
        ClientInfo clientInfo = new ClientInfo();
        PortInfo portInfo = new PortInfo();
        Map<Integer, PortInfo> portMap = new HashMap<>();
        portMap.put(80, portInfo);
        clientInfo.setPortMap(portMap);
        clientInfo.setAccessKey("68bfe8f0af124ecfa093350ab8d4b44f");
        portInfo.setAccessKey("68bfe8f0af124ecfa093350ab8d4b44f");
        portInfo.setPort(80);
        portInfo.setEndpoint("127.0.0.1:5122");
        serverHandler.saveClient(clientInfo);
    }

}
