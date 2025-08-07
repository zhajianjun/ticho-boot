package top.ticho.starter.cache.component;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import top.ticho.tool.core.TiStrUtil;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 缓存工具
 *
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
@AllArgsConstructor
public class TiCacheTemplate {

    /** 缓存管理器 */
    private CacheManager cacheManager;

    public Optional<Cache> getOptCache(String name) {
        if (TiStrUtil.isBlank(name)) {
            return Optional.empty();
        }
        return Optional.ofNullable(cacheManager.getCache(name));
    }

    public boolean contain(String name, Object key) {
        return getOptCache(name).map(x -> x.get(key)).map(Cache.ValueWrapper::get).isPresent();
    }

    public <T> T get(String name, Object key, Class<T> type) {
        return getOptCache(name).map(x -> x.get(key, type)).orElse(null);
    }

    public Object get(String name, Object key) {
        return getOptCache(name).map(x -> x.get(key)).map(Cache.ValueWrapper::get).orElse(null);
    }

    public <T> T get(String name, Object key, Callable<T> valueLoader) {
        return getOptCache(name).map(x -> x.get(key, valueLoader)).orElse(null);
    }

    public void put(String name, Object key, Object value) {
        getOptCache(name).ifPresent(x -> x.put(key, value));
    }

    /**
     * 删除缓存
     *
     * @param name 缓存名称
     * @param key  缓存键
     */
    public void evict(String name, Object key) {
        getOptCache(name).ifPresent(x -> x.evict(key));
    }

    /**
     * 清除缓存
     */
    public void clear(String name) {
        getOptCache(name).ifPresent(Cache::clear);
    }

    public long size(String name) {
        return getOptCache(name)
            .map(x -> (CaffeineCache) x)
            .map(CaffeineCache::getNativeCache)
            .map(com.github.benmanes.caffeine.cache.Cache::estimatedSize)
            .orElse(0L);
    }

}
