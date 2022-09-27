package com.ticho.boot.view.core;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;


/**
 * 用户信息
 *
 * @author zhajianjun
 * @date 2022-09-26 10:17:35
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "用户信息")
public class TichoSecurityUser implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "账户", position = 10)
    private String username;

    @ApiModelProperty(value = "密码", position = 20)
    private String password;

    @ApiModelProperty(value = "角色信息", position = 30)
    private List<String> roleIds;


    public String toString() {
        return getUsername();
    }

}
