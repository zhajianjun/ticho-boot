package top.ticho.boot.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import top.ticho.boot.cache.BaseCache;
import top.ticho.boot.redis.util.RedisUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author zhajianjun
 * @date 2023-02-06 15:43
 */
@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {

    /**
     * 缓存管理器
     *
     * @return {@link CacheManager}
     */
    @Bean
    @Primary
    public CacheManager cacheManager(List<BaseCache> baseCaches, RedisUtil<String, String> redisUtil) {
        List<CaffeineCache> caches = new ArrayList<>();
        for (BaseCache baseCache : baseCaches) {
            Cache<Object, Object> build = Caffeine.newBuilder()
                .recordStats()
                .expireAfterWrite(baseCache.getTtl(), TimeUnit.SECONDS)
                .maximumSize(baseCache.getMaxSize())
                .removalListener(new DefaultRemovalListener())
                .build();
            CaffeineCache caffeineCache = new CaffeineCache(baseCache.getName(), build);
            caches.add(caffeineCache);
        }
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Bean
    @ConditionalOnMissingBean(BaseCache.class)
    public BaseCache defaultBaseCache() {
        return new BaseCache("test", 1, 1);
    }

    static class DefaultRemovalListener implements RemovalListener<Object, Object> {

        @Override
        public void onRemoval(Object key, Object value, @NonNull RemovalCause cause) {
            log.info("缓存移除监听, 移除的key = {}, cause = {}", key, cause);
        }
    }

}
