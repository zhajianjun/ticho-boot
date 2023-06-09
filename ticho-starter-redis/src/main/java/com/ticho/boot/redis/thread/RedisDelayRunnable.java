package com.ticho.boot.redis.thread;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁 延时类 (守护线程)
 *
 * @author AdoroTutto
 * @date 2020-08-24 13:51
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
     * 锁失效时间ms
     */
    private final int expireTime;

    /**
     * 线程关闭的标记
     */
    private volatile Boolean signal;

    public RedisDelayRunnable(RLock rLock, String key, long threadId, int expireTime) {
        this.rLock = rLock;
        this.key = key;
        this.threadId = threadId;
        this.expireTime = expireTime;
        this.signal = Boolean.TRUE;
    }

    public void stop() {
        this.signal = Boolean.FALSE;
    }

    @Override
    public void run() {
        int delayTime = expireTime * 2 / 3;
        while (Boolean.TRUE.equals(signal)) {
            try {
                Thread.sleep(TimeUnit.SECONDS.toMillis(delayTime));
                //
                int wateTime = 5;
                //此处如果不使用tryLockAsync会导致锁id进行变更。导致释放锁的时候报错。expireTime才是过期时间，第一个是连接等待时间
                if (rLock.tryLockAsync(wateTime, expireTime, TimeUnit.SECONDS, threadId).get()) {
                    if (log.isInfoEnabled()) {
                        log.info("分布式锁延时成功，本次等待{}ms，将重置锁超时时间重置为{}s,其中threadId为{},key为{}", delayTime, expireTime, threadId, key);
                    }
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("expandLockTime 失败，将导致RedisDelay中断");
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
    }
}