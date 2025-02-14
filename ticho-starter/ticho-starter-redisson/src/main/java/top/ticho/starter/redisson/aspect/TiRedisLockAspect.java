package top.ticho.starter.redisson.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import top.ticho.starter.redisson.annotation.TiRedisLock;
import top.ticho.starter.redisson.util.TiRedissonTemplate;


/**
 * 分布式锁Aop
 * 该order必须设置
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Aspect
@Component
@Order(1)
@ConditionalOnClass(Aspect.class)
public class TiRedisLockAspect {

    @Autowired
    private TiRedissonTemplate tiRedissonTemplate;


    @Around("@annotation(tiRedisLock)")
    public Object around(ProceedingJoinPoint joinPoint, TiRedisLock tiRedisLock) throws Throwable {
        // 方法内的所有参数
        Object[] params = joinPoint.getArgs();
        int lockKey = tiRedisLock.lockKey();
        // 多久会自动释放，默认10秒
        int expireTime = tiRedisLock.expireTime();
        boolean delayThread = tiRedisLock.delayThread();
        // 取得方法名
        String key = joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName();
        // -1代表锁整个方法，而非具体锁哪条数据
        if (lockKey > 0) {
            key += params[lockKey - 1];
        }
        return tiRedissonTemplate.executeTb(key, expireTime, delayThread, joinPoint::proceed);
    }

}
