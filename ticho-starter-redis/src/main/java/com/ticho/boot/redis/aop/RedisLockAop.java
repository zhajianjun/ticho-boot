package com.ticho.boot.redis.aop;

import com.ticho.boot.redis.annotation.RedisLock;
import com.ticho.boot.redis.thread.RedisDelayRunnable;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.exception.BizException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;


/**
 * 分布式锁Aop
 * 该order必须设置
 *
 * @author zhajianjun
 * @date 2022-09-30 09:05:21
 */
@Aspect
@Component
@Order(1)
public class RedisLockAop {
    private static final Logger log = LoggerFactory.getLogger(RedisLockAop.class);

    @Autowired
    private Redisson redisson;

    @Autowired
    @Qualifier("asyncTaskExecutor")
    private Executor executor;


    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
        Object obj;
        // 方法内的所有参数
        Object[] params = joinPoint.getArgs();
        int lockKey = redisLock.lockKey();
        // 多久会自动释放，默认10秒
        int expireTime = redisLock.expireTime();
        boolean delayThread = redisLock.delayThread();
        // 取得方法名
        String key = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // -1代表锁整个方法，而非具体锁哪条数据
        if (lockKey > 0) {
            key += params[lockKey - 1];
        }
        RLock rLock = redisson.getLock(key);
        //定义锁的延时操作类
        RedisDelayRunnable redisDelayRunnable = null;
        //定义守护线程
        Thread redisDelayThread = null;
        boolean res = false;
        try {
            // 请求进来后需要等待多长时间去尝试获取锁
            int waitTime = 0;
            // 第二个参数 expireTime才是过期时间，第一个是连接等待时间
            res = rLock.tryLock(waitTime, expireTime, TimeUnit.SECONDS);
            if (res) {
                log.info("取到锁:{}", key);
                if (delayThread) {
                    redisDelayRunnable = new RedisDelayRunnable(rLock, key, Thread.currentThread().getId(), expireTime);
                    ThreadPoolTaskExecutor asyncExecutor = (ThreadPoolTaskExecutor) executor;
                    redisDelayThread = asyncExecutor.newThread(redisDelayRunnable);
                    redisDelayThread.setDaemon(Boolean.TRUE);
                    redisDelayThread.start();
                    log.info("创建守护线程:{}", redisDelayThread.getId());
                }
                obj = joinPoint.proceed();
            } else {
                log.info("获取锁:{}失败", key);
                throw new BizException(BizErrCode.FAIL, String.format("获取锁:%s失败", key));
            }
        } finally {
            if (null != redisDelayRunnable) {
                redisDelayRunnable.stop();
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
}
