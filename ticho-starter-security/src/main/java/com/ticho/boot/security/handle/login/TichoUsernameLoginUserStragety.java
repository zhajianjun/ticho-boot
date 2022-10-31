package com.ticho.boot.security.handle.login;

import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.handle.load.LoadUserService;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.view.util.Assert;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 用户密码登录
 *
 * @author zhajianjun
 * @date 2022-09-22 17:58
 */
@Component(SecurityConst.LOGIN_USER_TYPE_USERNAME)
@ConditionalOnMissingBean(name = SecurityConst.LOGIN_USER_TYPE_USERNAME)
@Primary
public class TichoUsernameLoginUserStragety implements LoginUserStragety {

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    @Qualifier(SecurityConst.LOAD_USER_TYPE_USERNAME)
    private LoadUserService loadUserService;

    @Override
    public TichoSecurityUser login(String account, String credentials) {
        // @formatter:off
        // 查询用户信息
        Assert.isNotNull(account, BizErrCode.PARAM_ERROR, "用户名不能为空");
        Assert.isNotNull(credentials, BizErrCode.PARAM_ERROR, "密码不能为空");
        TichoSecurityUser tichoSecurityUser = loadUserService.load(account);
        Assert.isNotNull(tichoSecurityUser, HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        // 校验用户密码
        String passwordAes = tichoSecurityUser.getPassword();
        Assert.isTrue(passwordEncoder.matches(credentials, passwordAes), HttpErrCode.NOT_LOGIN, "用户或者密码不正确");
        return tichoSecurityUser;
        // @formatter:on
    }

}
