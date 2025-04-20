package top.ticho.starter.security.core.jwt;

import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.util.CollectionUtils;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.dto.TiToken;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.util.TiAssert;
import top.ticho.tool.json.util.TiJsonUtil;

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
public record JwtEncode(TiSecurityProperty tiSecurityProperty, JwtSigner jwtSigner) {

    public JwtEncode {
        TiAssert.isNotNull(jwtSigner, TiBizErrCode.FAIL, "signer is null");
    }

    public void encode(TiToken oAuth2AccessToken, TiSecurityUser tiSecurityUser) {
        Signer signer = jwtSigner.getSigner();
        TiAssert.isNotNull(signer, TiBizErrCode.FAIL, "signer is null");
        long iat = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
        long accessTokenValidity = tiSecurityProperty.getAccessTokenValidity();
        long refreshTokenValidity = tiSecurityProperty.getRefreshTokenValidity();
        Long exp = iat + accessTokenValidity;
        Long refreTokenExp = iat + refreshTokenValidity;
        oAuth2AccessToken.setIat(iat);
        oAuth2AccessToken.setExp(exp);
        oAuth2AccessToken.setExpiresIn(oAuth2AccessToken.getExpiresIn());
        oAuth2AccessToken.setTokenType(TiSecurityConst.BEARER.toLowerCase());
        List<String> authorities = Optional.ofNullable(tiSecurityUser.getRoles()).orElseGet(ArrayList::new);
        // access token信息
        Map<String, Object> accessTokenInfo = new HashMap<>();
        accessTokenInfo.put(TiSecurityConst.TYPE, TiSecurityConst.ACCESS_TOKEN);
        accessTokenInfo.put(TiSecurityConst.EXP, exp);
        accessTokenInfo.put(TiSecurityConst.USERNAME, tiSecurityUser.getUsername());
        accessTokenInfo.put(TiSecurityConst.AUTHORITIES, authorities);
        // access token 加载扩展信息
        Map<String, Object> extInfo = oAuth2AccessToken.getExtInfo();
        if (!CollectionUtils.isEmpty(extInfo)) {
            accessTokenInfo.putAll(oAuth2AccessToken.getExtInfo());
        }
        // refresh token信息
        Map<String, Object> refreshTokenInfo = new HashMap<>();
        refreshTokenInfo.put(TiSecurityConst.TYPE, TiSecurityConst.REFRESH_TOKEN);
        refreshTokenInfo.put(TiSecurityConst.EXP, refreTokenExp);
        refreshTokenInfo.put(TiSecurityConst.USERNAME, tiSecurityUser.getUsername());
        String accessToken = JwtHelper.encode(TiJsonUtil.toJsonString(accessTokenInfo), signer).getEncoded();
        String refreshToken = JwtHelper.encode(TiJsonUtil.toJsonString(refreshTokenInfo), signer).getEncoded();
        oAuth2AccessToken.setAccessToken(accessToken);
        oAuth2AccessToken.setRefreshToken(refreshToken);
    }

}
