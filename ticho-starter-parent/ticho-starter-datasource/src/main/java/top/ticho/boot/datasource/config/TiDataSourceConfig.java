package top.ticho.boot.datasource.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.boot.datasource.prop.TiDataSourceProperty;

/**
 * 数据源配置
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Configuration
@PropertySource(value = "classpath:ticho-datasource.properties")
public class TiDataSourceConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.datasource")
    public TiDataSourceProperty tiDataSourceProperty() {
        return new TiDataSourceProperty();
    }


    /**
     * 注册一个StatViewServlet
     *
     * @return ServletRegistrationBean
     */
    @Bean
    @ConditionalOnMissingBean(name = "druidStatViewServle")
    public ServletRegistrationBean<StatViewServlet> druidStatViewServle() {
        // org.springframework.boot.context.embedded.ServletRegistrationBean提供类的进行注册.
        ServletRegistrationBean<StatViewServlet> servletRegistrationBean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        // 添加初始化参数：initParams
        // 白名单：
        servletRegistrationBean.addInitParameter("allow", "127.0.0.1");
        // IP黑名单 (存在共同时，deny优先于allow) : 如果满足deny的话提示:Sorry, you are not permitted to view this page.
        servletRegistrationBean.addInitParameter("deny", "192.168.0.114");
        // 登录查看信息的账号密码.
        servletRegistrationBean.addInitParameter("loginUsername", "admin");
        servletRegistrationBean.addInitParameter("loginPassword", "matecloud");
        // 是否能够重置数据.
        servletRegistrationBean.addInitParameter("resetEnable", "false");
        return servletRegistrationBean;
    }

    /**
     * 注册一个：filterRegistrationBean
     *
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnMissingBean(name = "druidStatFilter")
    public FilterRegistrationBean<WebStatFilter> druidStatFilter() {
        FilterRegistrationBean<WebStatFilter> filterRegistrationBean = new FilterRegistrationBean<>(new WebStatFilter());
        // 添加过滤规则.
        filterRegistrationBean.addUrlPatterns("/*");
        // 添加不需要忽略的格式信息.
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
        return filterRegistrationBean;
    }

}
