package com.ticho.boot.gateway.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author zhajianjun
 * @date 2023-02-08 09:06
 */
@ConfigurationProperties(prefix = "ticho.gateway.log")
@Component
@Data
public class BaseGatewayProperty {

    /** 是否打印日志 */
    private Boolean print = true;

}
