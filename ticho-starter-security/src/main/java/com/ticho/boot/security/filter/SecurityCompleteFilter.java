package com.ticho.boot.security.filter;

import com.ticho.boot.security.auth.AntPatternsAuthHandle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * security 后置处理过滤器
 * <p>1.清除线程变量==</p>
 *
 * @author zhajianjun
 * @date 2022-09-30 09:50
 */
public class SecurityCompleteFilter extends GenericFilterBean {

    @Autowired
    private AntPatternsAuthHandle antPatternsAuthHandle;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            chain.doFilter(request, response);
        } finally {
            antPatternsAuthHandle.clear();
        }
    }

}
