package top.ticho.starter.cache.config;

import com.github.benmanes.caffeine.cache.RemovalCause;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import top.ticho.starter.cache.prop.TiCacheProperty;

import java.util.Map;
import java.util.Set;

/**
 * @author zhajianjun
 * @date 2024-08-12 20:42
 */
@Slf4j
@AllArgsConstructor
public class DefaultTiCache implements TiCache<String, Object> {

    private final TiCacheProperty tiCacheProperty;

    @Override
    public String getName() {
        return tiCacheProperty.getName();
    }

    @Override
    public int getMaxSize() {
        return tiCacheProperty.getMaxSize();
    }

    @Override
    public int getTtl() {
        return tiCacheProperty.getTtl();
    }

    @Override
    public Map<String, Object> loadAll(Set<? extends String> keys) {
        return null;
    }

    @Override
    public void onRemoval(String key, Object value, RemovalCause cause) {
        log.info("缓存移除监听, 移除的key = {}, cause = {}", key, cause);
    }

}
