package top.ticho.boot.cache.config;

import java.util.List;

/**
 * @author zhajianjun
 * @date 2024-12-17 00:06
 */
public interface TiCacheBatch {

    List<TiCache<?, ?>> getTiCaches();

}
