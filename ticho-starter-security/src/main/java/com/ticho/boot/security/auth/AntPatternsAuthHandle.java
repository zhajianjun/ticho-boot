package com.ticho.boot.security.auth;

import com.ticho.boot.security.annotation.IgnoreJwtCheck;
import com.ticho.boot.security.annotation.IgnoreType;
import com.ticho.boot.security.constant.BaseOAuth2Const;
import com.ticho.boot.security.prop.BaseSecurityProperty;
import com.ticho.boot.web.util.SpringContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.method.HandlerMethod;

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
    private BaseSecurityProperty baseSecurityProperty;

    public boolean ignoreAuth(HttpServletRequest request) throws Exception {
        // @formatter:off
        if (ignoreHandleMethodJwtCheck(request)) {
            return true;
        }
        List<AntPathRequestMatcher> antPathRequestMatchers = baseSecurityProperty.getAntPathRequestMatchers();
        return antPathRequestMatchers.stream().anyMatch(x -> x.matches(request));
        // @formatter:on
    }

    /**
     * 忽略HandleMethod jwt校验
     *
     * @param request request
     * @return boolean
     * @throws Exception e
     */
    private boolean ignoreHandleMethodJwtCheck(HttpServletRequest request) throws Exception {
        HandlerMethod handlerMethod = SpringContext.getHandlerMethod(request);
        if (handlerMethod == null) {
            return false;
        }
        // IgnoreJwtCheck注解不存在返回false
        IgnoreJwtCheck methodAnnotation = handlerMethod.getMethodAnnotation(IgnoreJwtCheck.class);
        if (methodAnnotation == null) {
            return false;
        }
        // IgnoreAuth注解存在，且inner=false,则不是内部服务访问，则jwt校验完全放开
        IgnoreType value = methodAnnotation.value();
        if (IgnoreType.ALL.compareTo(value) == 0) {
            return true;
        }
        // inner=true,内部服务访问，则header中存在 inner = true,则权限放开
        return Objects.equals(request.getHeader(BaseOAuth2Const.INNER), BaseOAuth2Const.INNER_VALUE);
    }

}
