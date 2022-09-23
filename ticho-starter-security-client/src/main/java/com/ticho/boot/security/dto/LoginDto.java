package com.ticho.boot.security.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 登录请求参数
 *
 * @author zhajianjun
 * @date 2022-09-22 16:35
 */
@Data
@ApiModel("登录请求参数")
public class LoginDto {

    /** 用户名 */
    @ApiModelProperty(value = "用户名", position = 10)
    private String username;

    /** 密码 */
    @ApiModelProperty(value = "密码", position = 20)
    private String password;

    /** 登录类型 */
    @ApiModelProperty(value = "登录类型", position = 30)
    private String type;

}
