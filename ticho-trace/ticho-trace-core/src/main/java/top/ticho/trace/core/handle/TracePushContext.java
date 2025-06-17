package top.ticho.trace.core.handle;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.ThreadUtil;
import lombok.NoArgsConstructor;
import top.ticho.tool.json.util.TiJsonUtil;
import top.ticho.trace.common.bean.LogInfo;
import top.ticho.trace.common.bean.TraceInfo;
import top.ticho.trace.common.constant.LogConst;
import top.ticho.trace.common.prop.TiTraceProperty;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 链路推送上下文
 *
 * @author zhajianjun
 * @date 2024-02-01 12:30
 */
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TracePushContext {

    /** url地址匹配 */
    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();
    /** 线程池 */
    private static final ThreadPoolExecutor executor;

    private static final TracePushHandler pushHandler = data -> System.out.println(TiJsonUtil.toJsonString(data));

    static {
        float blockingCoefficient = 0.8F;
        int poolSize = (int) (Runtime.getRuntime().availableProcessors() / (1 - blockingCoefficient));
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory(LogConst.THREAD_NAME_PREFIX_TRACE, false);
        executor = ExecutorBuilder.create()
            .setCorePoolSize(poolSize)
            .setMaxPoolSize(poolSize)
            .setKeepAliveTime(0L)
            .setThreadFactory(threadFactory)
            .build();
    }

    /**
     * 推送日志信息
     *
     * @param url      url
     * @param logInfos 日志信息
     */
    public static void pushLogInfo(String url, String secret, List<LogInfo> logInfos) {
        pushHandler.push(logInfos);
    }

    /**
     * 异步推送链路信息
     *
     * @param traceInfo 跟踪信息
     */
    public static void asyncPushTrace(TiTraceProperty tiTraceProperty, TraceInfo traceInfo) {
        executor.execute(() -> pushTrace(tiTraceProperty, traceInfo));
    }

    /**
     * 推送链路信息
     *
     * @param traceInfo 跟踪信息
     */
    public static void pushTrace(TiTraceProperty tiTraceProperty, TraceInfo traceInfo) {
        String traceUrl = tiTraceProperty.getUrl();
        String secret = tiTraceProperty.getSecret();
        // 配置不推送链路信息
        if (!tiTraceProperty.getPushTrace()) {
            return;
        }
        // 不推送链路信息的url匹配
        List<String> antPatterns = tiTraceProperty.getAntPatterns();
        if (CollUtil.isEmpty(antPatterns)) {
            pushHandler.push(traceInfo);
        }
        String url = traceInfo.getUrl();
        boolean anyMatch = antPatterns.stream().anyMatch(x -> antPathMatcher.match(x, url));
        boolean isFirstSpanId = Objects.equals(LogConst.FIRST_SPAN_ID, traceInfo.getSpanId());
        // 如果匹配到的url是根节点则不推送链路信息
        if (anyMatch && isFirstSpanId) {
            return;
        }
        pushHandler.push(traceInfo);
    }

}
