package top.ticho.boot.cache.config;

import lombok.AllArgsConstructor;
import top.ticho.boot.cache.prop.CacheProperty;

import java.util.Collections;
import java.util.List;

/**
 * @author zhajianjun
 * @date 2024-12-17 00:23
 */
@AllArgsConstructor
public class DefaultBaseCacheBatch implements BaseCacheBatch {
    private final CacheProperty cacheProperty;

    @Override
    public List<BaseCache> getBaseCaches() {
        return Collections.singletonList(new DefaultBaseCache(cacheProperty));
    }

}
