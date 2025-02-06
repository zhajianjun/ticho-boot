package top.ticho.starter.log.filter;

import top.ticho.starter.log.wrapper.TiRequestWrapper;
import top.ticho.starter.log.wrapper.TiResponseWrapper;

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
public class TiWapperRequestFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) {
    }

    /**
     * 执行过滤操作
     * <p>
     * 本方法的主要目的是对传入的请求和响应对象进行包装，以便在后续的过滤链中使用自定义的请求和响应对象
     * 如果请求和响应分别是HttpServletRequest和HttpServletResponse的实例，那么就创建相应的包装对象
     * 这样做可以允许在过滤链中的其他过滤器或最终的目标资源中访问和操作请求和响应的特定内容
     *
     * @param servletRequest  请求对象，用于获取请求信息
     * @param servletResponse 响应对象，用于发送响应信息
     * @param filterChain     过滤链，用于将请求传递给下一个过滤器或目标资源
     * @throws IOException      如果在执行过滤过程中发生I/O错误
     * @throws ServletException 如果在执行过滤过程中发生Servlet相关的错误
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 初始化请求包装器
        ServletRequest requestWrapper = null;
        // 如果请求是一个HTTP请求，则创建一个自定义的请求包装器
        if (servletRequest instanceof HttpServletRequest) {
            requestWrapper = new TiRequestWrapper((HttpServletRequest) servletRequest);
        }
        // 初始化响应包装器
        ServletResponse responseWrapper = null;
        // 如果响应是一个HTTP响应，则创建一个自定义的响应包装器
        if (servletResponse instanceof HttpServletResponse) {
            responseWrapper = new TiResponseWrapper((HttpServletResponse) servletResponse);
        }
        // 如果请求包装器被成功创建，则使用它来替换原始的请求对象
        if (requestWrapper != null) {
            servletRequest = requestWrapper;
        }
        // 如果响应包装器被成功创建，则使用它来替换原始的响应对象
        if (responseWrapper != null) {
            servletResponse = responseWrapper;
        }
        // 将包装后的请求和响应对象传递给过滤链中的下一个元素
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }

}
