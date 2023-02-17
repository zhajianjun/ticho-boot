package com.ticho.boot.redisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Redis锁
 *
 * @author zhajianjun
 * @date 2023-02-17 16:42
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedisLock {
    /**
     * 要锁该方法的第几个参数作为key
     */
    int lockKey() default 0;

    /**
     * 锁失效时间（单位：秒），默认10s
     */
    int expireTime() default 10;

    /**
     * 是否开启守护线程来延时redis锁
     */
    boolean delayThread() default true;

}
