package top.ticho.boot.redisson.thread;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁 延时类 (守护线程)
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
public class RedisDelayRunnable implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(RedisDelayRunnable.class);

    private final RLock rLock;

    /**
     * redis锁的key
     */
    private final String key;

    /**
     * 线程的id
     */
    private final long threadId;

    /**
     * 锁失效时间,单位:秒(s)
     */
    private final int expireTime;

    /**
     * 线程关闭的标记
     */
    private volatile Boolean signal;

    /** mdc数据 */
    private Map<String, String> mdcMap;

    public RedisDelayRunnable(RLock rLock, String key, long threadId, int expireTime, Map<String, String> mdcMap) {
        this.rLock = rLock;
        this.key = key;
        this.threadId = threadId;
        this.expireTime = expireTime;
        this.signal = Boolean.TRUE;
        this.mdcMap = mdcMap;
    }

    public void stop() {
        this.signal = Boolean.FALSE;
    }

    @Override
    public void run() {
        // @formatter:off
        MDC.setContextMap(mdcMap);
        // 等待时间
        int delayTime = expireTime * 2 / 3;
        while (Boolean.TRUE.equals(signal)) {
            try {
                Thread thread = Thread.currentThread();
                long id = thread.getId();
                // 超过等待时间，则延迟锁时间
                Thread.sleep(TimeUnit.SECONDS.toMillis(delayTime));
                //
                int wateTime = 5;
                //此处如果不使用tryLockAsync会导致锁id进行变更。导致释放锁的时候报错。expireTime才是过期时间，第一个是连接等待时间
                if (rLock.tryLockAsync(wateTime, expireTime, TimeUnit.SECONDS, threadId).get()) {
                    if (log.isInfoEnabled()) {
                        log.info("分布式锁[{}]延时成功，等待{}ms，锁超时时间重置为{}s,主线程{},守护线程{}", key, delayTime, expireTime, threadId, id);
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("分布式锁[{}]延时失败，守护线程{}中断", key, id);
                    }
                    this.stop();
                }
            } catch (InterruptedException e) {
                if (log.isInfoEnabled()) {
                    log.debug("RedisDelayRunnable 处理线程被强制中断");
                }
            } catch (Exception e) {
                log.error("RedisDelayRunnable run error", e);
            }
        }
        if (log.isInfoEnabled()) {
            log.debug("RedisDelayRunnable 处理线程已停止");
        }
        MDC.clear();
        // @formatter:on
    }
}