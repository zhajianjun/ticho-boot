package top.ticho.starter.security.dto;

/**
 * 登录请求参数接口
 *
 * @author zhajianjun
 * @date 2022-09-22 16:35
 */
public interface LoginRequest {

    /** 用户名 */
    String getUsername();

    void setUsername(String username);

    /** 密码 */
    String getPassword();

    void setPassword(String password);

}
