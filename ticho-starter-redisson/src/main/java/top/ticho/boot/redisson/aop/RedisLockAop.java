package top.ticho.boot.redisson.aop;

import top.ticho.boot.redisson.annotation.RedisLock;
import top.ticho.boot.redisson.util.RedissonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


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
public class RedisLockAop {

    @Autowired
    private RedissonUtil redissonUtil;


    @Around("@annotation(redisLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedisLock redisLock) throws Throwable {
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
        return redissonUtil.executeTb(key, expireTime, delayThread, joinPoint::proceed);
    }

}
