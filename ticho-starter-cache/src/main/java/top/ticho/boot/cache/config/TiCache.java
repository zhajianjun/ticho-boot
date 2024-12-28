package top.ticho.boot.cache.config;

import com.github.benmanes.caffeine.cache.RemovalCause;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

/**
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
public interface TiCache {

    /** 缓存名称 */
    String getName();

    /** 最大存储数量 */
    int getMaxSize();

    /** 过期时间 */
    int getTtl();

    default Object load(Object key) throws Exception {
        return null;
    }

    default Map<Object, Object> loadAll(Iterable<?> keys) throws Exception {
        return Collections.emptyMap();
    }

    default void onRemoval(Object key, Object value, RemovalCause cause) {

    }

    /**
     * 创建后过期时间
     *
     * @param key         key
     * @param value       value
     * @param currentTime 当前时间 （单位:ns纳秒）
     * @return 过期剩余时间（单位:ns纳秒），以纳秒为单位
     */
    default long expireAfterCreate(Object key, Object value, long currentTime) {
        int ttl = getTtl();
        return Duration.ofMillis(ttl).toNanos() + currentTime;
    }

    /**
     * 更新后过期时间
     *
     * @param key             key
     * @param value           value
     * @param currentTime     当前时间 （单位:ns纳秒）
     * @param currentDuration 当前持续时间
     * @return 过期剩余时间（单位:ns纳秒），以纳秒为单位
     */
    default long expireAfterUpdate(Object key, Object value, long currentTime, long currentDuration) {
        return currentDuration;
    }

    /**
     * 读取后过期时间
     *
     * @param key             key
     * @param value           value
     * @param currentTime     当前时间 （单位:ns纳秒）
     * @param currentDuration 当前持续时间
     * @return 过期剩余时间（单位:ns纳秒），以纳秒为单位
     */
    default long expireAfterRead(Object key, Object value, long currentTime, long currentDuration) {
        return currentDuration;
    }

}
