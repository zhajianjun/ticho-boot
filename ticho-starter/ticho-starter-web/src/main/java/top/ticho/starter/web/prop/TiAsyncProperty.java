package top.ticho.starter.web.prop;

import lombok.Data;

/**
 * @author zhajianjun
 * @date 2023-01-12 11:08
 */
@Data
public class TiAsyncProperty {

    /** 开启异步线程池 */
    private boolean enableAsync = true;
    /** 核心线程数，线程池维护线程的最小数量. */
    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    /** 最大线程数，线程池维护线程的最大数量 */
    private int maxPoolSize = corePoolSize * 2;
    /** 队列容量 */
    private int queueCapacity = 20;
    /** 线程活跃时间（秒） */
    private int keepAliveSeconds = 60;
    /** 设置线程名称前缀 */
    private String threadNamePrefix = "ticho";

}
