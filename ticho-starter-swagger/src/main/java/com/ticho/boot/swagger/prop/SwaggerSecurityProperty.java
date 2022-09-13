package com.ticho.boot.swagger.prop;

import com.ticho.boot.swagger.config.DefaultSwaggerConfig;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-05 14:09
 */
@Configuration
@ConditionalOnBean(DefaultSwaggerConfig.class)
@ConfigurationProperties(prefix = "ticho.swagger")
public class SwaggerSecurityProperty {

    /** 权限地址 */
    @ApiModelProperty(value = "权限地址", position = 10)
    private String securityUrl;

    public String getSecurityUrl() {
        return securityUrl;
    }

    public void setSecurityUrl(String securityUrl) {
        this.securityUrl = securityUrl;
    }
}
