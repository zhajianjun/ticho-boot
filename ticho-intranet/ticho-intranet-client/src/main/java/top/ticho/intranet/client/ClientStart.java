package top.ticho.intranet.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.client.handler.ClientHander;
import top.ticho.intranet.common.prop.ClientProperty;
import top.ticho.tool.json.util.TiJsonUtil;

import java.io.File;
import java.util.Objects;


/**
 * 客户端启动器
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@Slf4j
public class ClientStart {

    public static void main(String[] args) {
        Configurator.initialize("Client", "config/log4j2.xml");
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator + "/config/client.yaml";
        ClientProperty clientProperty;
        try {
            clientProperty = TiJsonUtil.toJavaObjectFromProperty(new File(filePath), ClientProperty.class);
        } catch (Exception e) {
            log.error("配置文件获取失败，{}", e.getMessage(), e);
            return;
        }
        if (Objects.isNull(clientProperty)) {
            log.error("配置文件不存在");
            return;
        }
        log.info("配置信息：{}", TiJsonUtil.toJsonString(clientProperty));
        ClientHander clientHander = new ClientHander(clientProperty);
        clientHander.start();
    }

}