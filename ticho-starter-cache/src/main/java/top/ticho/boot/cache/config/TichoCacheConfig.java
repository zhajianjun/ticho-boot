package top.ticho.boot.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.index.qual.NonNegative;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.lang.NonNull;
import top.ticho.boot.cache.component.CacheTemplate;
import top.ticho.boot.cache.prop.CacheProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 缓存配置
 *
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "ticho.cache.enable", havingValue = "true")
@EnableCaching
public class TichoCacheConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.cache")
    public CacheProperty cacheProperty() {
        return new CacheProperty();
    }

    @Bean
    public BaseCache defaultBaseCache(CacheProperty cacheProperty) {
        return new DefaultBaseCache(cacheProperty);
    }

    /**
     * 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager(List<BaseCache> baseCaches) {
        List<CaffeineCache> caches = new ArrayList<>();
        for (BaseCache baseCache : baseCaches) {
            Cache<Object, Object> build = getObjectObjectCaffeine(baseCache);
            CaffeineCache caffeineCache = new CaffeineCache(baseCache.getName(), build);
            caches.add(caffeineCache);
        }
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    @Bean
    public CacheTemplate cacheTemplate(CacheManager cacheManager) {
        return new CacheTemplate(cacheManager);
    }


    private Cache<Object, Object> getObjectObjectCaffeine(BaseCache baseCache) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .recordStats()
            .expireAfter(getExpiry(baseCache))
            .maximumSize(baseCache.getMaxSize())
            .removalListener(baseCache::onRemoval);
        return caffeine.build(new CacheLoader<Object, Object>() {
            @Override
            public Object load(@NonNull Object key) throws Exception {
                return baseCache.load(key);
            }

            @Override
            @NonNull
            public Map<Object, Object> loadAll(@NonNull Iterable<?> keys) throws Exception {
                return baseCache.loadAll(keys);
            }
        });
    }

    private Expiry<Object, Object> getExpiry(BaseCache baseCache) {
        return new Expiry<Object, Object>() {
            @Override
            public long expireAfterCreate(@NonNull Object key, @NonNull Object value, long currentTime) {
                return baseCache.expireAfterCreate(key, value, currentTime);
            }

            @Override
            public long expireAfterUpdate(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                return baseCache.expireAfterUpdate(key.toString(), value, currentTime, currentDuration);
            }

            @Override
            public long expireAfterRead(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                return baseCache.expireAfterRead(key.toString(), value, currentTime, currentDuration);
            }
        };
    }

}
