package com.ticho.boot.security.prop;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * security参数配置对象
 *
 * @author zhajianjun
 * @date 2022-09-23 14:46
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ticho.oauth")
@Slf4j
public class TichoOauthProperty {

    /** ccess_token的有效时间(秒) 默认12小时 */
    private Integer accessTokenValidity = 43200;

    /** refresh_token有效期(秒) 默认24小时 */
    private Integer refreshTokenValidity = 86400;


}
