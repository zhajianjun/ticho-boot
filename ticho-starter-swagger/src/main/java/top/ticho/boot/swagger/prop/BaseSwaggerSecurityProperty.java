package top.ticho.boot.swagger.prop;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty(value = "开启swagger功能", position = 10)
    private Boolean enable = false;

    /** 权限地址 */
    @ApiModelProperty(value = "权限地址", position = 20)
    private String securityUrl;

    /** 权限类型 */
    @ApiModelProperty(value = "权限类型", position = 30)
    private String securityType;

}
