package top.ticho.starter.redisson.util;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import top.ticho.starter.redisson.thread.TiRedisDelayRunnable;
import top.ticho.starter.view.enums.TiBizErrorCode;
import top.ticho.starter.view.exception.TiBizException;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Slf4j
@AllArgsConstructor
public class TiRedissonTemplate {

    private final Redisson redisson;

    private final Executor executor;

    public <T> T execute(String key, int expireTime, Supplier<T> supplier) {
        return execute(key, expireTime, true, supplier);
    }

    public <T> T execute(String key, int expireTime, boolean delayThread, Supplier<T> supplier) {
        T obj;
        RLock rLock = null;
        // 定义锁的延时操作类
        TiRedisDelayRunnable tiRedisDelayRunnable = null;
        // 定义守护线程
        Thread redisDelayThread = null;
        boolean res = false;
        try {
            rLock = redisson.getLock(key);
            // 请求进来后需要等待多长时间去尝试获取锁
            int waitTime = 0;
            // 第二个参数 expireTime才是过期时间，第一个是连接等待时间
            res = rLock.tryLock(waitTime, expireTime, TimeUnit.SECONDS);
            if (res) {
                log.info("取到锁:{}", key);
                if (delayThread) {
                    Map<String, String> mdcMap = MDC.getCopyOfContextMap();
                    tiRedisDelayRunnable = new TiRedisDelayRunnable(rLock, key, Thread.currentThread().getId(), expireTime, mdcMap);
                    ThreadPoolTaskExecutor asyncExecutor = (ThreadPoolTaskExecutor) executor;
                    redisDelayThread = asyncExecutor.newThread(tiRedisDelayRunnable);
                    redisDelayThread.setDaemon(Boolean.TRUE);
                    redisDelayThread.start();
                    log.info("守护线程{}创建成功,当前线程{}", redisDelayThread.getId(), Thread.currentThread().getId());
                }
                obj = supplier.get();
            } else {
                log.info("获取锁:{}失败", key);
                throw new TiBizException(TiBizErrorCode.FAIL, String.format("获取锁:%s失败", key));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TiBizException(e.getMessage());
        } finally {
            if (null != tiRedisDelayRunnable) {
                tiRedisDelayRunnable.stop();
            }
            if (null != redisDelayThread) {
                redisDelayThread.interrupt();
            }
            if (res) {
                rLock.unlock();
                log.info("释放锁:{}", key);
            }
        }
        return obj;
    }


    public <T> T executeTb(String key, int expireTime, boolean delayThread, TaSupplier<T> supplier) throws Throwable {
        T obj;
        RLock rLock = null;
        // 定义锁的延时操作类
        TiRedisDelayRunnable tiRedisDelayRunnable = null;
        // 定义守护线程
        Thread redisDelayThread = null;
        boolean res = false;
        try {
            rLock = redisson.getLock(key);
            // 请求进来后需要等待多长时间去尝试获取锁
            int waitTime = 0;
            // 第二个参数 expireTime才是过期时间，第一个是连接等待时间
            res = rLock.tryLock(waitTime, expireTime, TimeUnit.SECONDS);
            if (res) {
                log.info("取到锁:{}", key);
                if (delayThread) {
                    Map<String, String> mdcMap = MDC.getCopyOfContextMap();
                    tiRedisDelayRunnable = new TiRedisDelayRunnable(rLock, key, Thread.currentThread().getId(), expireTime, mdcMap);
                    ThreadPoolTaskExecutor asyncExecutor = (ThreadPoolTaskExecutor) executor;
                    redisDelayThread = asyncExecutor.newThread(tiRedisDelayRunnable);
                    redisDelayThread.setDaemon(Boolean.TRUE);
                    redisDelayThread.start();
                    log.info("守护线程{}创建成功,当前线程{}", redisDelayThread.getId(), Thread.currentThread().getId());
                }
                obj = supplier.get();
            } else {
                log.info("获取锁:{}失败", key);
                throw new TiBizException(TiBizErrorCode.FAIL, String.format("获取锁:%s失败", key));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TiBizException(e.getMessage());
        } finally {
            if (null != tiRedisDelayRunnable) {
                tiRedisDelayRunnable.stop();
            }
            if (null != redisDelayThread) {
                redisDelayThread.interrupt();
            }
            if (res) {
                rLock.unlock();
                log.info("释放锁:{}", key);
            }
        }
        return obj;
    }

    @FunctionalInterface
    public interface TaSupplier<T> {

        /**
         * Gets a result.
         *
         * @return a result
         */
        T get() throws Throwable;
    }


}
