package top.ticho.boot.web.util;

import lombok.Getter;
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
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * Spring 工具
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */

@Component
@Slf4j
public class SpringContext implements ApplicationContextAware {

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (SpringContext.applicationContext == null) {
            SpringContext.applicationContext = applicationContext;
        }
    }

    public static void publishEvent(ApplicationEvent event) {
        applicationContext.publishEvent(event);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }


    public static HandlerMethod getHandlerMethod(HttpServletRequest request) {
        RequestMappingHandlerMapping mapping = getBean(RequestMappingHandlerMapping.class);
        HandlerExecutionChain executionChain;
        try {
            executionChain = mapping.getHandler(request);
        } catch (Exception e) {
            log.warn("get handler method error, {}", e.getMessage());
            return null;
        }
        if (executionChain == null) {
            return null;
        }
        // 不是handler，false
        Object handler = executionChain.getHandler();
        if (!(handler instanceof HandlerMethod)) {
            return null;
        }
        return (HandlerMethod) handler;
    }

    public static HandlerMethod getHandlerMethod() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return getHandlerMethod(request);
    }

    public static Object registerSingletonBean(String beanName, Object singletonObject) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
        beanFactory.registerSingleton(beanName, singletonObject);
        return applicationContext.getBean(beanName);
    }

    public static boolean addBean(String beanName, Class<?> beanClass, Object... constructValues) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) applicationContext).getBeanFactory();
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

    public static void removeBean(String beanName) {
        BeanDefinitionRegistry beanDefReg = (DefaultListableBeanFactory) ((AbstractRefreshableApplicationContext) applicationContext).getBeanFactory();
        beanDefReg.removeBeanDefinition(beanName);
    }

}
