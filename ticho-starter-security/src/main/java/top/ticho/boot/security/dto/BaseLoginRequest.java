package top.ticho.boot.security.dto;

import lombok.Data;

/**
 * 登录请求参数
 *
 * @author zhajianjun
 * @date 2022-09-22 16:35
 */
@Data
public class BaseLoginRequest implements LoginRequest {

    /** 用户名 */
    private String username;
    /** 密码 */
    private String password;
}
