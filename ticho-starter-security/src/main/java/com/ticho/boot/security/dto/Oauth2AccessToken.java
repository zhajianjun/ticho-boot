package com.ticho.boot.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ticho.boot.security.constant.SecurityConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * token信息
 *
 * @author zhajianjun
 * @date 2022-09-21 16:52
 */
@Data
@ApiModel("Oauth2AccessToken信息")
public class Oauth2AccessToken {

    /** token */
    @ApiModelProperty(value = "token", position = 10)
    @JsonProperty(SecurityConst.ACCESS_TOKEN)
    public String accessToken;

    /** refresh token  */
    @ApiModelProperty(value = "refresh token", position = 20)
    @JsonProperty(SecurityConst.REFRESH_TOKEN)
    public String refreshToken;

    /** token type  */
    @ApiModelProperty(value = "token type", position = 20)
    @JsonProperty(SecurityConst.TOKEN_TYPE)
    public String tokenType;

    /** 开始时间戳，单位(s) */
    @ApiModelProperty(value = "开始时间戳，单位(s)", position = 30)
    @JsonProperty(SecurityConst.IAT)
    public Long iat;

    /** 剩余时间，单位(s)  */
    @ApiModelProperty(value = "剩余时间，单位(s)", position = 40)
    @JsonProperty(SecurityConst.EXPIRES_IN)
    public Long expiresIn;

    /** 结束时间时间戳，单位(s)  */
    @ApiModelProperty(value = "结束时间时间戳，单位(s)", position = 50)
    @JsonProperty(SecurityConst.EXP)
    public Long exp;

    @ApiModelProperty(value = "自定义扩展信息", position = 60)
    @JsonIgnore
    public Map<String, Object> extInfo;


    @JsonIgnore
    public boolean isExpired() {
        return exp < TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }

    public Long getExpiresIn() {
        return exp - TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
    }
}
