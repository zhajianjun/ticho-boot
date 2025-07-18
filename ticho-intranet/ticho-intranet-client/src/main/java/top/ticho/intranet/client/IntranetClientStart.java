package top.ticho.intranet.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.core.config.Configurator;
import top.ticho.intranet.client.core.IntranetClientBuilder;
import top.ticho.intranet.client.core.IntranetClientHandler;
import top.ticho.intranet.common.prop.IntranetClientProperty;
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
public class IntranetClientStart {

    public static void main(String[] args) {
        Configurator.initialize("Client", "config/log4j2.xml");
        String projectPath = System.getProperty("user.dir");
        String filePath = projectPath + File.separator + "/config/client.yaml";
        IntranetClientProperty intranetClientProperty;
        try {
            intranetClientProperty = TiJsonUtil.toObjectFromProperty(new File(filePath), IntranetClientProperty.class);
        } catch (Exception e) {
            log.error("配置文件获取失败，{}", e.getMessage(), e);
            return;
        }
        if (Objects.isNull(intranetClientProperty)) {
            log.error("配置文件不存在");
            return;
        }
        log.info("配置信息：{}", TiJsonUtil.toJsonString(intranetClientProperty));
        IntranetClientHandler intranetClientHandler = IntranetClientBuilder.build(intranetClientProperty);
        intranetClientHandler.start();
    }

}