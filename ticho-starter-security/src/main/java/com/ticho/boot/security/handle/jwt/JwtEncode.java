package com.ticho.boot.security.handle.jwt;

import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.security.constant.BaseSecurityConst;
import com.ticho.boot.security.dto.Oauth2AccessToken;
import com.ticho.boot.security.prop.BaseOauthProperty;
import com.ticho.boot.view.enums.BizErrCode;
import com.ticho.boot.view.core.BaseSecurityUser;
import com.ticho.boot.view.util.Assert;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * jwt加密
 *
 * @author zhajianjun
 * @date 2022-09-24 13:45:19
 */
public class JwtEncode {

    public final JwtSigner jwtSigner;
    public final BaseOauthProperty oauthProperty;

    public JwtEncode(JwtSigner jwtSigner, BaseOauthProperty oauthProperty) {
        Assert.isNotNull(jwtSigner, BizErrCode.FAIL, "signer is null");
        this.jwtSigner = jwtSigner;
        this.oauthProperty = oauthProperty;
    }

    public void encode(Oauth2AccessToken oAuth2AccessToken, BaseSecurityUser baseSecurityUser) {
        // @formatter:off
        Signer signer = jwtSigner.getSigner();
        Assert.isNotNull(signer, BizErrCode.FAIL, "signer is null");
        long iat = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long accessTokenValidity = oauthProperty.getAccessTokenValidity();
        long refreshTokenValidity = oauthProperty.getRefreshTokenValidity();
        Long exp = iat + accessTokenValidity;
        Long refreTokenExp = iat + refreshTokenValidity;
        oAuth2AccessToken.setIat(iat);
        oAuth2AccessToken.setExp(exp);
        oAuth2AccessToken.setExpiresIn(oAuth2AccessToken.getExpiresIn());
        oAuth2AccessToken.setTokenType(BaseSecurityConst.BEARER.toLowerCase());
        List<String> authorities = Optional.ofNullable(baseSecurityUser.getRoles()).orElseGet(ArrayList::new);
        // access token信息
        Map<String, Object> accessTokenInfo = new HashMap<>();
        accessTokenInfo.put(BaseSecurityConst.TYPE, BaseSecurityConst.ACCESS_TOKEN);
        accessTokenInfo.put(BaseSecurityConst.EXP, exp);
        accessTokenInfo.put(BaseSecurityConst.USERNAME, baseSecurityUser.getUsername());
        accessTokenInfo.put(BaseSecurityConst.AUTHORITIES, authorities);
        // access token 加载扩展信息
        Map<String, Object> extInfo = oAuth2AccessToken.getExtInfo();
        if (!CollectionUtils.isEmpty(extInfo)) {
            accessTokenInfo.putAll(oAuth2AccessToken.getExtInfo());
        }
        // refresh token信息
        Map<String, Object> refreshTokenInfo = new HashMap<>();
        refreshTokenInfo.put(BaseSecurityConst.TYPE, BaseSecurityConst.REFRESH_TOKEN);
        refreshTokenInfo.put(BaseSecurityConst.EXP, refreTokenExp);
        refreshTokenInfo.put(BaseSecurityConst.USERNAME, baseSecurityUser.getUsername());
        String accessToken = JwtHelper.encode(JsonUtil.toJsonString(accessTokenInfo), signer).getEncoded();
        String refreshToken = JwtHelper.encode(JsonUtil.toJsonString(refreshTokenInfo), signer).getEncoded();
        oAuth2AccessToken.setAccessToken(accessToken);
        oAuth2AccessToken.setRefreshToken(refreshToken);
        // @formatter:on
    }

}
