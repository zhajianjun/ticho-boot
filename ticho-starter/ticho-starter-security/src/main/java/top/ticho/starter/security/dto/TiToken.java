package top.ticho.starter.security.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import top.ticho.starter.security.constant.TiSecurityConst;

import java.util.Map;

/**
 * Token信息
 *
 * @author zhajianjun
 * @date 2022-09-21 16:52
 */
@Data
public class TiToken {

    /** token */
    @JsonProperty(TiSecurityConst.ACCESS_TOKEN)
    public String accessToken;
    /** refresh token */
    @JsonProperty(TiSecurityConst.REFRESH_TOKEN)
    public String refreshToken;
    /** token type */
    @JsonProperty(TiSecurityConst.TOKEN_TYPE)
    public String tokenType;
    @JsonIgnore
    public Map<String, Object> extInfo;

}
