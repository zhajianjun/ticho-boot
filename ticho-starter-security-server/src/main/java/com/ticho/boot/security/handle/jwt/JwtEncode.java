package com.ticho.boot.security.handle.jwt;

import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.dto.OAuth2AccessToken;
import com.ticho.boot.security.prop.TichoOauthProperty;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.JsonUtil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * jwt加密
 *
 * @author zhajianjun
 * @date 2022-09-24 13:45:19
 */
public class JwtEncode {

    public final JwtConverter jwtConverter;
    public final TichoOauthProperty oauthProperty;

    public JwtEncode(JwtConverter jwtConverter, TichoOauthProperty oauthProperty) {
        Assert.isNotNull(jwtConverter, BizErrCode.FAIL, "signer is null");
        this.jwtConverter = jwtConverter;
        this.oauthProperty = oauthProperty;
    }

    public void encode(OAuth2AccessToken oAuth2AccessToken, UserDetails userDetails) {
        // @formatter:off
        Signer signer = jwtConverter.getSigner();
        Assert.isNotNull(signer, BizErrCode.FAIL, "signer is null");
        long iat = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long accessTokenValidity = oauthProperty.getAccessTokenValidity();
        long refreshTokenValidity = oauthProperty.getRefreshTokenValidity();
        Long exp = iat + accessTokenValidity;
        Long refreTokenExp = iat + refreshTokenValidity;
        oAuth2AccessToken.setIat(iat);
        oAuth2AccessToken.setExp(exp);
        oAuth2AccessToken.setExpiresIn(oAuth2AccessToken.getExpiresIn());
        List<String> authorities = Optional.ofNullable(userDetails.getAuthorities()).orElseGet(ArrayList::new)
            .stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());
        Map<String, Object> accessTokenInfo = new HashMap<>();
        accessTokenInfo.put(OAuth2Const.TYPE, OAuth2Const.ACCESS_TOKEN);
        accessTokenInfo.put(OAuth2Const.EXP, exp);
        accessTokenInfo.put(OAuth2Const.USERNAME, userDetails.getUsername());
        accessTokenInfo.put(OAuth2Const.AUTHORITIES, authorities);
        Map<String, Object> extInfo = oAuth2AccessToken.getExtInfo();
        if (!CollectionUtils.isEmpty(extInfo)) {
            accessTokenInfo.putAll(oAuth2AccessToken.getExtInfo());
        }
        Map<String, Object> refreshTokenInfo = new HashMap<>();
        refreshTokenInfo.put(OAuth2Const.TYPE, OAuth2Const.REFRESH_TOKEN);
        refreshTokenInfo.put(OAuth2Const.EXP, refreTokenExp);
        refreshTokenInfo.put(OAuth2Const.USERNAME, userDetails.getUsername());
        String accessToken = JwtHelper.encode(JsonUtil.toJsonString(accessTokenInfo), signer).getEncoded();
        String refreshToken = JwtHelper.encode(JsonUtil.toJsonString(refreshTokenInfo), signer).getEncoded();
        oAuth2AccessToken.setAccessToken(accessToken);
        oAuth2AccessToken.setRefreshToken(refreshToken);
        // @formatter:on
    }

}
