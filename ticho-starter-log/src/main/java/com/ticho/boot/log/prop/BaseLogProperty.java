package com.ticho.boot.log.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 日志配置
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@ConfigurationProperties(prefix = "ticho.log")
@Component
@Data
public class BaseLogProperty {

    /** 是否开启日志拦截器 */
    private Boolean enable = true;

    /** 是否打印日志 */
    private Boolean print = false;

    /** 日志打印前缀 */
    private String reqPrefix = "[REQ]";

}
