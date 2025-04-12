package top.ticho.starter.http.interceptor;

import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ticho.starter.http.constant.TiHttpConst;

/**
 * @author zhajianjun
 * @date 2022-09-26 16:20:40
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(RequestInterceptor.class)
public class TiFeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        // feign则是内部调用，添加header inner = true
        template.header(TiHttpConst.INNER, TiHttpConst.INNER_VALUE);
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String authorization = null;
        if (requestAttributes != null) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        }
        if (StrUtil.isBlank(authorization)) {
            authorization = MDC.get(HttpHeaders.AUTHORIZATION);
        }
        template.header(HttpHeaders.AUTHORIZATION, authorization);
    }

}
