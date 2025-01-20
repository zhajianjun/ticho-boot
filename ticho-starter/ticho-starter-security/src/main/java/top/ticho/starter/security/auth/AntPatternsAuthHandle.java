package top.ticho.starter.security.auth;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import top.ticho.starter.security.annotation.IgnoreJwtCheck;
import top.ticho.starter.security.annotation.IgnoreType;
import top.ticho.starter.security.constant.BaseOAuth2Const;
import top.ticho.starter.security.prop.BaseSecurityProperty;
import top.ticho.boot.web.util.TiSpringUtil;

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
    /** security参数配置对象 */
    private final BaseSecurityProperty baseSecurityProperty;
    /** url地址匹配 */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public AntPatternsAuthHandle(BaseSecurityProperty baseSecurityProperty) {
        this.baseSecurityProperty = baseSecurityProperty;
    }

    public boolean ignoreAuth(HttpServletRequest request) {
        if (ignoreHandleMethodJwtCheck(request)) {
            return true;
        }
        List<String> antPatterns = baseSecurityProperty.getAntPatterns();
        return antPatterns.stream().anyMatch(x -> antPathMatcher.match(x, request.getRequestURI()));
    }

    /**
     * 忽略HandleMethod jwt校验
     *
     * @param request request
     * @return boolean
     */
    private boolean ignoreHandleMethodJwtCheck(HttpServletRequest request) {
        HandlerMethod handlerMethod = TiSpringUtil.getHandlerMethod(request);
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
