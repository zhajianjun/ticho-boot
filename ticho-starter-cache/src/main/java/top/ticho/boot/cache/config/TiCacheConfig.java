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
import top.ticho.boot.cache.component.TiCacheTemplate;
import top.ticho.boot.cache.prop.TiCacheProperty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
public class TiCacheConfig {

    @Bean
    @ConfigurationProperties(prefix = "ticho.cache")
    public TiCacheProperty cacheProperty() {
        return new TiCacheProperty();
    }

    @Bean
    public TiCache tiCache(TiCacheProperty tiCacheProperty) {
        return new DefaultTiCache(tiCacheProperty);
    }

    @Bean
    public TiCacheBatch tiChcheBatch(TiCacheProperty tiCacheProperty) {
        return new DefaultTiCacheBatch(tiCacheProperty);
    }

    /**
     * 缓存管理器
     */
    @Bean
    @Primary
    public CacheManager cacheManager(List<TiCache> tiCaches, List<TiCacheBatch> tiCacheBatches) {
        List<TiCache> tiCachesCollect = tiCacheBatches
            .stream()
            .map(TiCacheBatch::getTiCaches)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());
        List<CaffeineCache> caches = Stream
            .concat(tiCaches.stream(), tiCachesCollect.stream())
            .collect(Collectors.toMap(TiCache::getName, Function.identity(), (o, n) -> o))
            .values()
            .stream()
            .map(this::buildCaffeineCache)
            .collect(Collectors.toList());
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildCaffeineCache(TiCache tiCache) {
        return new CaffeineCache(tiCache.getName(), buildCache(tiCache));
    }

    @Bean
    public TiCacheTemplate tiCacheTemplate(CacheManager cacheManager) {
        return new TiCacheTemplate(cacheManager);
    }


    private Cache<Object, Object> buildCache(TiCache tiCache) {
        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
            .recordStats()
            .expireAfter(getExpiry(tiCache))
            .maximumSize(tiCache.getMaxSize())
            .removalListener(tiCache::onRemoval);
        return caffeine.build(new CacheLoader<Object, Object>() {
            @Override
            public Object load(@NonNull Object key) throws Exception {
                return tiCache.load(key);
            }

            @Override
            @NonNull
            public Map<Object, Object> loadAll(@NonNull Iterable<?> keys) throws Exception {
                return tiCache.loadAll(keys);
            }
        });
    }

    private Expiry<Object, Object> getExpiry(TiCache tiCache) {
        return new Expiry<Object, Object>() {
            @Override
            public long expireAfterCreate(@NonNull Object key, @NonNull Object value, long currentTime) {
                return tiCache.expireAfterCreate(key, value, currentTime);
            }

            @Override
            public long expireAfterUpdate(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                return tiCache.expireAfterUpdate(key.toString(), value, currentTime, currentDuration);
            }

            @Override
            public long expireAfterRead(@NonNull Object key, @NonNull Object value, long currentTime, @NonNegative long currentDuration) {
                return tiCache.expireAfterRead(key.toString(), value, currentTime, currentDuration);
            }
        };
    }

}
