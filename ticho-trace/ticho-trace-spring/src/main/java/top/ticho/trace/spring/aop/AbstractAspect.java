package top.ticho.trace.spring.aop;

import cn.hutool.core.util.StrUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import top.ticho.trace.common.TiHttpTraceTag;
import top.ticho.trace.common.TiTraceContext;
import top.ticho.trace.common.TiTraceProperty;
import top.ticho.trace.spring.util.IpUtil;

import jakarta.annotation.Resource;

/**
 * 通用链路处理
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
public abstract class AbstractAspect implements Ordered {

    @Resource
    private Environment environment;

    @Resource
    private TiTraceProperty tiTraceProperty;

    public Object trace(ProceedingJoinPoint joinPoint, String appName) throws Throwable {
        if (StrUtil.isNotBlank(TiTraceContext.getTraceId())) {
            return joinPoint.proceed();
        }
        try {
            TiTraceContext.start(appName, tiTraceProperty.getTrace());
            TiTraceContext.addTag(TiHttpTraceTag.IP, IpUtil.localIp());
            TiTraceContext.addTag(TiHttpTraceTag.ENV, environment.getProperty("spring.profiles.active"));
            return joinPoint.proceed();
        } finally {
            TiTraceContext.close();
        }
    }


}
