package top.ticho.starter.web.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Spring 工具类，提供了一些常用的 Spring 相关的方法。
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Component
@Slf4j
public class TiSpringUtil implements ApplicationContextAware {

    /**
     * 存储当前的 ApplicationContext 实例。
     */
    public static final AtomicReference<ApplicationContext> APPLICATION_CONTEXT_ATOMIC_REFERENCE = new AtomicReference<>();

    /**
     * 设置 ApplicationContext 实例。
     *
     * @param applicationContext 应用上下文实例
     * @throws BeansException 如果设置失败
     */
    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        TiSpringUtil.APPLICATION_CONTEXT_ATOMIC_REFERENCE.compareAndSet(null, applicationContext);
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get();
    }

    /**
     * 发布一个应用事件。
     *
     * @param event 应用事件
     */
    public static void publishEvent(ApplicationEvent event) {
        APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().publishEvent(event);
    }

    /**
     * 根据名称获取 Bean。
     *
     * @param name Bean 名称
     * @return Bean 实例
     */
    public static Object getBean(String name) {
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getBean(name);
    }

    /**
     * 根据类型获取 Bean。
     *
     * @param clazz Bean 类型
     * @param <T>   泛型类型
     * @return Bean 实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getBean(clazz);
    }

    /**
     * 根据名称和类型获取 Bean。
     *
     * @param name  Bean 名称
     * @param clazz Bean 类型
     * @param <T>   泛型类型
     * @return Bean 实例
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getBean(name, clazz);
    }

    /**
     * 获取 RequestMappingHandlerMapping 实例。
     *
     * @return RequestMappingHandlerMapping 实例
     */
    private static RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getBean(RequestMappingHandlerMapping.class);
    }

    /**
     * 根据请求获取 HandlerMethod。
     *
     * @param request HTTP 请求
     * @return HandlerMethod 实例
     */
    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        RequestMappingHandlerMapping mapping = getRequestMappingHandlerMapping();
        HandlerExecutionChain executionChain;
        try {
            executionChain = mapping.getHandler(request);
        } catch (Exception e) {
            log.error("Failed to get handler method for request: {}", request, e);
            throw new RuntimeException("Failed to get handler method", e);
        }
        if (executionChain == null) {
            return null;
        }
        Object handler = executionChain.getHandler();
        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod;
        } else {
            log.warn("Handler is not an instance of HandlerMethod: {}", handler.getClass());
            return null;
        }
    }

    /**
     * 获取当前请求的 HandlerMethod。
     *
     * @return HandlerMethod 实例
     */
    public static HandlerMethod getHandlerMethod() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return getHandlerMethod(request);
    }

    /**
     * 注册一个单例 Bean。
     *
     * @param beanName        单例 Bean 名称
     * @param singletonObject 单例对象
     * @return 注册的 Bean 实例
     */
    public static Object registerSingletonBean(String beanName, Object singletonObject) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(beanName, singletonObject);
        return APPLICATION_CONTEXT_ATOMIC_REFERENCE.get().getBean(beanName);
    }

    /**
     * 添加一个新的 Bean 定义。
     *
     * @param beanName        Bean 名称
     * @param beanClass       Bean 类型
     * @param constructValues 构造参数值
     * @return 是否成功添加
     */
    public static boolean addBean(String beanName, Class<?> beanClass, Object... constructValues) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) APPLICATION_CONTEXT_ATOMIC_REFERENCE.get()).getBeanFactory();
        if (beanDefReg.containsBeanDefinition(beanName)) {
            return false;
        }
        BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        for (Object constructValue : constructValues) {
            beanDefBuilder.addConstructorArgValue(constructValue);
        }
        BeanDefinition beanDefinition = beanDefBuilder.getBeanDefinition();
        beanDefReg.registerBeanDefinition(beanName, beanDefinition);
        return true;
    }

    /**
     * 移除一个 Bean 定义。
     *
     * @param beanName Bean 名称
     */
    public static void removeBean(String beanName) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) APPLICATION_CONTEXT_ATOMIC_REFERENCE.get()).getBeanFactory();
        beanDefReg.removeBeanDefinition(beanName);
    }

}
