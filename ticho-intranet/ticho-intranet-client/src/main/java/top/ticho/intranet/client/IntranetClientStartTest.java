package top.ticho.intranet.client;

import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.client.core.IntranetClientBuilder;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.prop.IntranetClientProperty;


/**
 * 客户端启动测试
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public class IntranetClientStartTest {

    public static void main(String[] args) {
        Configurator.initialize("Client", "config/log4j2.xml");
        IntranetClientProperty intranetClientProperty = new IntranetClientProperty();
        intranetClientProperty.setAccessKey("68bfe8f0af124ecfa093350ab8d4b44f");
        intranetClientProperty.setServerHost("127.0.0.1");
        intranetClientProperty.setServerPort(5120);
        intranetClientProperty.setSslEnable(false);
        IntranetClientHandler intranetClientHandler = IntranetClientBuilder.build(intranetClientProperty);
        intranetClientHandler.start();
    }

}