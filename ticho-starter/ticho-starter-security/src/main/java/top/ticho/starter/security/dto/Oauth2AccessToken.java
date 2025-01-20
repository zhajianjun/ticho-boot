package top.ticho.starter.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import top.ticho.starter.security.constant.BaseSecurityConst;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Oauth2AccessToken信息
 *
 * @author zhajianjun
 * @date 2022-09-21 16:52
 */
@Data
public class Oauth2AccessToken {

    /** token */
    @JsonProperty(BaseSecurityConst.ACCESS_TOKEN)
    public String accessToken;
    /** refresh token */
    @JsonProperty(BaseSecurityConst.REFRESH_TOKEN)
    public String refreshToken;
    /** token type */
    @JsonProperty(BaseSecurityConst.TOKEN_TYPE)
    public String tokenType;
    /** 开始时间戳，单位(s) */
    @JsonProperty(BaseSecurityConst.IAT)
    public Long iat;
    /** 剩余时间，单位(s) */
    @JsonProperty(BaseSecurityConst.EXPIRES_IN)
    public Long expiresIn;
    /** 结束时间时间戳，单位(s) */
    @JsonProperty(BaseSecurityConst.EXP)
    public Long exp;
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
