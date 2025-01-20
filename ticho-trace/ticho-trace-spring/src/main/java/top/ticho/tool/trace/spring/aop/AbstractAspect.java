package top.ticho.tool.trace.spring.aop;

import cn.hutool.core.date.SystemClock;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import top.ticho.tool.trace.common.bean.TraceInfo;
import top.ticho.tool.trace.common.constant.LogConst;
import top.ticho.tool.trace.common.prop.TraceProperty;
import top.ticho.tool.trace.core.handle.TracePushContext;
import top.ticho.tool.trace.core.util.TraceUtil;
import top.ticho.tool.trace.spring.event.TraceEvent;
import top.ticho.tool.trace.spring.util.IpUtil;

import javax.annotation.Resource;

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
    private TraceProperty traceProperty;

    public Object trace(ProceedingJoinPoint joinPoint, String preAppName, String preIp) throws Throwable {
        if (StrUtil.isNotBlank(MDC.get(LogConst.TRACE_ID_KEY))) {
            return joinPoint.proceed();
        }
        long start = SystemClock.now();
        try {
            String appName = environment.getProperty("spring.application.name");
            String ip = IpUtil.localIp();
            TraceUtil.prepare(null, null, appName, ip, preAppName, preIp, null);
            return joinPoint.proceed();
        } finally {
            String env = environment.getProperty("spring.profiles.active");
            long end = SystemClock.now();
            Long consume = end - start;
            TraceInfo traceInfo = TraceInfo.builder()
                .traceId(MDC.get(LogConst.TRACE_ID_KEY))
                .spanId(MDC.get(LogConst.SPAN_ID_KEY))
                .appName(MDC.get(LogConst.APP_NAME_KEY))
                .env(env)
                .ip(MDC.get(LogConst.IP_KEY))
                .preAppName(MDC.get(LogConst.PRE_APP_NAME_KEY))
                .preIp(MDC.get(LogConst.PRE_IP_KEY))
                // .url(url)
                // .port(port)
                // .method(handlerMethod.toString())
                // .type(type)
                // .status(status)
                .start(start)
                .end(end)
                .consume(consume)
                .build();
            TracePushContext.asyncPushTrace(traceProperty, traceInfo);
            ApplicationContext applicationContext = SpringUtil.getApplicationContext();
            applicationContext.publishEvent(new TraceEvent(applicationContext, traceInfo));
            TraceUtil.complete();
        }
    }


}
