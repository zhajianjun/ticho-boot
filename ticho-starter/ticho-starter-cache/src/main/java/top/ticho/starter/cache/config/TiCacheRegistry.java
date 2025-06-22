package top.ticho.starter.cache.config;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2025-06-21 07:57
 */
public interface TiCacheRegistry {

    void register(List<TiCache<?, ?>> tiCaches);

}
