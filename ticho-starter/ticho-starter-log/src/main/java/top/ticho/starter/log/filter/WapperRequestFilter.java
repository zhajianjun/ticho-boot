package top.ticho.starter.log.filter;

import top.ticho.starter.log.wrapper.RequestWrapper;
import top.ticho.starter.log.wrapper.ResponseWrapper;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * web过滤器
 *
 * @author zhajianjun
 * @date 2023-01-11 10:25
 */
@WebFilter(urlPatterns = "/*", filterName = "wapperRequestFilter")
public class WapperRequestFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        ServletRequest requestWrapper = null;
        if (servletRequest instanceof HttpServletRequest) {
            requestWrapper = new RequestWrapper((HttpServletRequest) servletRequest);
        }
        ServletResponse responseWrapper = null;
        if (servletResponse instanceof HttpServletResponse) {
            responseWrapper = new ResponseWrapper((HttpServletResponse) servletResponse);
        }
        if (requestWrapper != null) {
            servletRequest = requestWrapper;
        }
        if (responseWrapper != null) {
            servletResponse = responseWrapper;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
