package com.ticho.boot.log.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Miniio连接对象
 *
 * @author zhajianjun
 * @date 2022-07-13 22:40:25
 */
@ConfigurationProperties(prefix = "ticho.log")
@Component
@Data
public class TichoLogProperty {

    /** 是否开启日志拦截器 */
    private Boolean enable = false;

    /** 是否打印日志 */
    private Boolean print = true;

    /** 日志打印前缀 */
    private String requestPrefixText = "[REQUEST]";

}
