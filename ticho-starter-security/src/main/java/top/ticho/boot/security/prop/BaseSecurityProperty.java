package top.ticho.boot.security.prop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import top.ticho.boot.view.core.BaseSecurityUser;
import top.ticho.boot.web.util.SpringContext;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * security参数配置对象
 *
 * @author zhajianjun
 * @date 2022-09-23 14:46
 */
@Getter
@NoArgsConstructor
@Slf4j
public class BaseSecurityProperty {

    /** 默认用户，如果UserDetailsService接口被实现则没有啥作用了 */
    private List<BaseSecurityUser> users = new ArrayList<>();

    /** 权限过滤地址 */
    @Setter
    private List<String> antPatterns = new ArrayList<>();

    public void setUsers(List<BaseSecurityUser> users) {
        // @formatter:off
        if (CollUtil.isEmpty(users)) {
            return;
        }
        PasswordEncoder passwordEncoder = SpringContext.getBean(PasswordEncoder.class);
        for (BaseSecurityUser userInfo : users) {
            String password = userInfo.getPassword();
            if (StrUtil.isBlank(password)) {
                continue;
            }
            userInfo.setPassword(passwordEncoder.encode(password));
        }
        this.users = users;
        // @formatter:on
    }

}
