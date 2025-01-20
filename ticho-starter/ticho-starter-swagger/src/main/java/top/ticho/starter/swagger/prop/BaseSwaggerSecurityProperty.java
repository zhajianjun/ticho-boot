package top.ticho.starter.swagger.prop;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * swagger参数配置
 *
 * @author zhajianjun
 * @date 2022-09-05 14:09
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "ticho.swagger")
public class BaseSwaggerSecurityProperty {

    /** 开启swagger功能 */
    /** 开启swagger功能 */
    private Boolean enable = false;

    /** 权限地址 */
    /** 权限地址 */
    private String securityUrl;

    /** 权限类型 */
    /** 权限类型 */
    private String securityType;

}
