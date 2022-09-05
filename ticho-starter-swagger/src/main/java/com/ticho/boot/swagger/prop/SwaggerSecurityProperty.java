package com.ticho.boot.swagger.prop;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-05 14:09
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ticho.security")
public class SwaggerSecurityProperty {

    /** 权限地址 */
    @ApiModelProperty(value = "权限地址", position = 10)
    private String url;

}
