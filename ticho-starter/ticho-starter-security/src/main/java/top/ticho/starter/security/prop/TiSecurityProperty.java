package top.ticho.starter.security.prop;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.web.util.TiSpringUtil;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiCollUtil;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.security.TiSm2Util;

import java.security.KeyPair;
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
public class TiSecurityProperty implements InitializingBean {

    /** 公钥 */
    private String publicKey;
    /** 私钥 */
    private String privateKey;
    /** access_token的有效时间(秒) 默认12小时 */
    private Integer accessTokenValidity = 43200;
    /** refresh_token有效期(秒) 默认24小时 */
    private Integer refreshTokenValidity = 86400;
    /** 是否开启默认接口, 缺省为false */
    private Web web;
    /** 默认用户，如果TiLoginService接口被实现则没有啥作用了 */
    private List<TiSecurityUser> users = new ArrayList<>();
    /** 权限过滤地址 */
    private List<String> antPatterns = new ArrayList<>();

    public void setUsers(List<TiSecurityUser> users) {
        if (TiCollUtil.isEmpty(users)) {
            return;
        }
        PasswordEncoder passwordEncoder = TiSpringUtil.getBean(PasswordEncoder.class);
        for (TiSecurityUser userInfo : users) {
            String password = userInfo.getPassword();
            if (TiStrUtil.isBlank(password)) {
                continue;
            }
            userInfo.setPassword(passwordEncoder.encode(password));
        }
        this.users = users;
    }

    @Data
    public static class Web {
        private Boolean enable;
    }

    @Override
    public void afterPropertiesSet() {
        if (TiStrUtil.isEmpty(this.publicKey) || TiStrUtil.isEmpty(this.privateKey)) {
            KeyPair keyPair = TiSm2Util.generateSM2KeyPair();
            this.publicKey = TiBase64Util.encode(keyPair.getPublic().getEncoded());
            this.privateKey = TiBase64Util.encode(keyPair.getPrivate().getEncoded());
            log.info("生成密钥对成功。\n公钥: [{}]\n私钥: [{}]", this.publicKey, this.privateKey);
        }
    }


}
