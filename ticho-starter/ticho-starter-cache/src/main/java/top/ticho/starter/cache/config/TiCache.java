package top.ticho.starter.cache.config;

import com.github.benmanes.caffeine.cache.RemovalCause;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
public interface TiCache<K, V> {

    /** 缓存名称 */
    String getName();

    /** 最大存储数量 */
    int getMaxSize();

    /** 过期时间 */
    int getTtl();

    default V load(String key) {
        return null;
    }

    default Map<K, V> loadAll(Set<? extends K> keys) {
        return Collections.emptyMap();
    }

    default void onRemoval(K key, V value, RemovalCause cause) {

    }

    /**
     * 创建后过期时间
     *
     * @param key         key
     * @param value       value
     * @param currentTime 当前时间 （单位:ns纳秒）
     * @return 过期剩余时间（单位:ns纳秒），以纳秒为单位
     */
    default long expireAfterCreate(K key, V value, long currentTime) {
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
    default long expireAfterUpdate(K key, V value, long currentTime, long currentDuration) {
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
    default long expireAfterRead(K key, V value, long currentTime, long currentDuration) {
        return currentDuration;
    }

}
