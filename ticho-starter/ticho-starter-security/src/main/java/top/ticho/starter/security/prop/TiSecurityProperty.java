package top.ticho.starter.security.prop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.web.util.TiSpringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * security参数配置对象
 *
 * @author zhajianjun
 * @date 2022-09-23 14:46
 */
@Data
@Slf4j
public class TiSecurityProperty {

    /** access_token的有效时间(秒) 默认12小时 */
    private Integer accessTokenValidity = 43200;
    /** refresh_token有效期(秒) 默认24小时 */
    private Integer refreshTokenValidity = 86400;
    /** 默认用户，如果UserDetailsService接口被实现则没有啥作用了 */
    private List<TiSecurityUser> users = new ArrayList<>();
    /** 权限过滤地址 */
    private List<String> antPatterns = new ArrayList<>();

    public void setUsers(List<TiSecurityUser> users) {
        if (CollUtil.isEmpty(users)) {
            return;
        }
        PasswordEncoder passwordEncoder = TiSpringUtil.getBean(PasswordEncoder.class);
        for (TiSecurityUser userInfo : users) {
            String password = userInfo.getPassword();
            if (StrUtil.isBlank(password)) {
                continue;
            }
            userInfo.setPassword(passwordEncoder.encode(password));
        }
        this.users = users;
    }

}
