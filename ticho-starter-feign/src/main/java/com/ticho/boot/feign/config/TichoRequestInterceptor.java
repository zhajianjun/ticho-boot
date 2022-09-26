package com.ticho.boot.feign.config;

import cn.hutool.core.collection.CollUtil;
import com.ticho.boot.feign.constant.FeignConst;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-09-26 16:20:40
 */
@Slf4j
@Configuration
@ConditionalOnMissingBean(RequestInterceptor.class)
public class TichoRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header(FeignConst.INNER, FeignConst.INNER_VALUE);

        Collection<String> fromHeader = template.headers().get(FeignConst.INNER);
        // 带from 请求直接跳过
        if (CollUtil.isNotEmpty(fromHeader) && fromHeader.contains(FeignConst.INNER_VALUE)) {
            return;
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        template.header(HttpHeaders.AUTHORIZATION, request.getHeader(HttpHeaders.AUTHORIZATION));

    }

}
