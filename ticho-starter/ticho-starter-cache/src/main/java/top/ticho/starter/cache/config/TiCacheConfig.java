package top.ticho.starter.cache.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import top.ticho.starter.cache.component.TiCacheTemplate;
import top.ticho.starter.cache.prop.TiCacheProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 缓存配置
 *
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
@Slf4j
@Configuration
@ConditionalOnProperty(value = "ticho.cache.enable", havingValue = "true", matchIfMissing = true)
@EnableCaching
public class TiCacheConfig {

    /**
     * 创建并配置缓存属性
     *
     * @return 缓存属性对象
     */
    @Bean
    @ConfigurationProperties(prefix = "ticho.cache")
    public TiCacheProperty cacheProperty() {
        return new TiCacheProperty();
    }

    /**
     * 创建缓存操作模板
     *
     * @param tiCacheProperty 缓存属性
     * @return 缓存操作模板对象
     */
    @Bean
    public TiCache<String, Object> tiCache(TiCacheProperty tiCacheProperty) {
        return new DefaultTiCacheRegistry(tiCacheProperty);
    }

    /**
     * 创建缓存管理器
     *
     * @return 缓存注册器列表
     */
    @Bean
    @Primary
    public CacheManager cacheManager(List<TiCacheRegistry> tiCacheRegistries) {
        List<TiCache<?, ?>> tiCaches = new ArrayList<>();
        for (TiCacheRegistry tiCacheRegistry : tiCacheRegistries) {
            tiCacheRegistry.register(tiCaches);
        }
        List<String> names = new ArrayList<>();
        List<CaffeineCache> caches = tiCaches
            .stream()
            .filter(item -> validRepeat(item, names))
            .map(this::buildCaffeineCache)
            .collect(Collectors.toList());
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private boolean validRepeat(TiCache<?, ?> item, List<String> names) {
        if (names.contains(item.getName())) {
            log.warn("缓存名称重复：{}", item.getName());
            return false;
        }
        names.add(item.getName());
        return true;
    }

    /**
     * 创建缓存模板
     *
     * @param cacheManager 缓存管理器
     * @return 缓存模板对象
     */
    @Bean
    public TiCacheTemplate tiCacheTemplate(CacheManager cacheManager) {
        return new TiCacheTemplate(cacheManager);
    }

    /**
     * 构建Caffeine缓存
     *
     * @param tiCache 缓存操作模板
     * @return Caffeine缓存对象
     */
    private CaffeineCache buildCaffeineCache(TiCache<?, ?> tiCache) {
        Cache<Object, Object> cache = (Cache<Object, Object>) buildCache(tiCache);
        return new CaffeineCache(tiCache.getName(), cache);
    }

    /**
     * 构建缓存
     *
     * @param tiCache 缓存操作模板
     * @return 缓存对象
     */
    private <K, V> Cache<K, V> buildCache(TiCache<K, V> tiCache) {
        Caffeine<K, V> caffeine = Caffeine.newBuilder()
            .recordStats()
            .expireAfter(getExpiry(tiCache))
            .maximumSize(tiCache.getMaxSize())
            .removalListener(tiCache::onRemoval);
        return caffeine.build(new CacheLoader<K, V>() {
            @Override
            public V load(@NonNull K key) {
                return tiCache.load(key.toString());
            }

            @Override
            @NonNull
            public Map<K, V> loadAll(@NonNull Set<? extends K> keys) {
                return tiCache.loadAll(keys);
            }

        });
    }

    /**
     * 获取缓存过期策略
     *
     * @param tiCache 缓存操作模板
     * @return 缓存过期策略对象
     */
    private <K, V> Expiry<K, V> getExpiry(TiCache<K, V> tiCache) {
        return new Expiry<K, V>() {
            /**
             * 在创建缓存项时，确定其过期时间
             * 此方法用于指定新创建缓存项的过期时间，基于当前时间和缓存项的键值
             *
             * @param key 缓存项的键，用于唯一标识缓存中的项
             * @param value 缓存项的值，与键关联存储
             * @param currentTime 当前时间，用作计算过期时间的基准
             * @return 返回缓存项的过期时间，以毫秒为单位
             */
            @Override
            public long expireAfterCreate(@NonNull K key, @NonNull V value, long currentTime) {
                return tiCache.expireAfterCreate(key, value, currentTime);
            }


            /**
             * 在更新操作后设置过期时间
             * 当缓存项被更新时，此方法用于确定新的过期时间它基于当前时间和当前持续时间来计算
             *
             * @param key 缓存项的键，用于唯一标识缓存中的项
             * @param value 缓存项的值，可以是任何类型的数据
             * @param currentTime 当前时间，通常用于计算过期时间
             * @param currentDuration 当前缓存项的持续时间，即缓存项在更新前的剩余有效时间
             * @return 返回新的过期时间，单位取决于具体实现
             */
            @Override
            public long expireAfterUpdate(@NonNull K key, @NonNull V value, long currentTime, long currentDuration) {
                // 调用tiCache的expireAfterUpdate方法来设置新的过期时间
                return tiCache.expireAfterUpdate(key, value, currentTime, currentDuration);
            }


            /**
             * 在读取操作后设置缓存项的过期时间
             * 此方法覆盖了基类或接口的expireAfterRead方法，旨在为缓存项设置读取后的过期时间
             * 它考虑了当前时间和当前持续时间，以决定缓存项的过期时间
             *
             * @param key 缓存项的键，用于唯一标识缓存中的项，不能为空
             * @param value 缓存项的值，用于存储与键关联的数据，不能为空
             * @param currentTime 当前时间，用于计算缓存项的过期时间
             * @param currentDuration 当前持续时间，表示缓存项当前的过期时间，必须为非负数
             * @return 返回新的过期时间，以毫秒为单位
             */
            @Override
            public long expireAfterRead(@NonNull K key, @NonNull V value, long currentTime, long currentDuration) {
                // 调用tiCache的expireAfterRead方法，传入转换为字符串的键、值、当前时间和当前持续时间
                // 并返回新的过期时间
                return tiCache.expireAfterRead(key, value, currentTime, currentDuration);
            }
        };
    }

}
