package top.ticho.boot.view.core;


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
public class TiSecurityUser implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 账户名 */
    private String username;

    /** 密码 */
    private String password;

    /** 角色编码信息 */
    private List<String> roles;

    /** token */
    private String token;


    public String toString() {
        return getUsername();
    }

}
