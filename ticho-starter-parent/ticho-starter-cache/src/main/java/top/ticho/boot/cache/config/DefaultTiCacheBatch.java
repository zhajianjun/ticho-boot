package top.ticho.boot.cache.config;

import lombok.AllArgsConstructor;
import top.ticho.boot.cache.prop.TiCacheProperty;

import java.util.Collections;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2024-12-17 00:23
 */
@AllArgsConstructor
public class DefaultTiCacheBatch implements TiCacheBatch {
    private final TiCacheProperty tiCacheProperty;

    @Override
    public List<TiCache<?, ?>> getTiCaches() {
        return Collections.singletonList(new DefaultTiCache(tiCacheProperty));
    }

}
