package top.ticho.starter.web.util;


import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * id工具类
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Component
@RefreshScope
public class TiIdUtil implements InitializingBean {
    private static Snowflake snowflake = IdUtil.getSnowflake(0, 0);

    @Value("${spring.application.serviceId:0}")
    private Long serviceId;

    @Value("${spring.application.datacenterId:0}")
    private Long datacenterId;


    public static Long getId() {
        return snowflake.nextId();
    }

    public static String getIdStr() {
        return snowflake.nextIdStr();
    }

    public static String getSimpleUuid() {
        return IdUtil.fastSimpleUUID();
    }

    public static String getUuid() {
        return IdUtil.fastUUID();
    }

    @Override
    public void afterPropertiesSet() {
        snowflake = IdUtil.getSnowflake(serviceId, datacenterId);
    }
}
