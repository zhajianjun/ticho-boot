package com.ticho.boot.security.auth;

import com.ticho.boot.security.annotation.IgnoreAuth;
import com.ticho.boot.security.constant.OAuth2Const;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.web.util.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 接口匹配过滤处理
 *
 * @author zhajianjun
 * @date 2022-09-26 15:22
 */
public class AntPatternsAuthHandle {

    @Autowired
    private TichoSecurityProperty tichoSecurityProperty;

    public boolean ignoreAuth(HttpServletRequest request) throws Exception {
        // @formatter:off
        if (checkHandleMethodIsIgnoreAuth(request)) {
            return true;
        }
        List<AntPathRequestMatcher> antPathRequestMatchers = tichoSecurityProperty.getAntPathRequestMatchers();
        return antPathRequestMatchers.stream().anyMatch(x -> x.matches(request));
        // @formatter:on
    }

    /**
     * 检查HandleMethod token校验注解
     *
     * @param request request
     * @return boolean
     * @throws Exception e
     */
    private boolean checkHandleMethodIsIgnoreAuth(HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = SpringContext.getHandlerMethod(request);
        if (handlerMethod == null) {
            return false;
        }
        // 没有IgnoreAuth注解，false
        IgnoreAuth methodAnnotation = handlerMethod.getMethodAnnotation(IgnoreAuth.class);
        if (methodAnnotation == null) {
            return false;
        }
        // IgnoreAuth注解存在，且inner=false,则不是内部服务访问，则jwt校验完全放开
        boolean inner = methodAnnotation.value();
        if (!inner) {
            return true;
        }
        // inner=true,内部服务访问，则header中存在 inner = true,则权限放开
        return Objects.equals(request.getHeader(OAuth2Const.INNER), OAuth2Const.INNER_VALUE);
    }

}
