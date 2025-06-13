package top.ticho.starter.security.auth;

import org.springframework.util.AntPathMatcher;
import org.springframework.web.method.HandlerMethod;
import top.ticho.starter.security.annotation.IgnoreJwtCheck;
import top.ticho.starter.security.annotation.IgnoreType;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.web.util.TiSpringUtil;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * 接口匹配过滤处理
 *
 * @author zhajianjun
 * @date 2022-09-26 15:22
 */
public class TiAntPatternsAuthHandle {
    /** security参数配置对象 */
    private final TiSecurityProperty tiSecurityProperty;
    /** url地址匹配 */
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public TiAntPatternsAuthHandle(TiSecurityProperty tiSecurityProperty) {
        this.tiSecurityProperty = tiSecurityProperty;
    }

    public boolean ignoreAuth(HttpServletRequest request) {
        if (ignoreHandleMethodJwtCheck(request)) {
            return true;
        }
        List<String> antPatterns = tiSecurityProperty.getAntPatterns();
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
        return Objects.equals(request.getHeader(TiSecurityConst.INNER), TiSecurityConst.INNER_VALUE);
    }

}
