package top.ticho.starter.cache.config;

import lombok.AllArgsConstructor;
import top.ticho.starter.cache.prop.TiCacheProperty;

import java.util.Collections;
import java.util.List;

/**
 * 默认的批量缓存实现类，用于管理缓存实例集合
 *
 * @author zhajianjun
 * @date 2024-12-17 00:23
 */
@AllArgsConstructor
public class DefaultTiCacheBatch implements TiCacheBatch {
    /** 缓存配置属性对象，用于初始化缓存实例 */
    private final TiCacheProperty tiCacheProperty;

    /**
     * 获取缓存实例集合
     * 方法通过单例模式返回包含默认缓存实例的不可变列表，
     * 该默认缓存实例使用注入的配置属性进行初始化
     *
     * @return 包含唯一默认缓存实例的不可修改列表
     */
    @Override
    public List<TiCache<?, ?>> getTiCaches() {
        // 使用单元素列表模式返回默认缓存实例，保证返回集合的不可修改性
        return Collections.singletonList(new DefaultTiCache(tiCacheProperty));
    }

}
