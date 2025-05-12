package top.ticho.starter.http.fallback;

import feign.Target;
import lombok.AllArgsConstructor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cloud.openfeign.FallbackFactory;

/**
 * 默认fallback
 *
 * @author zhajianjun
 * @date 2022-08-06 14:09
 */
@AllArgsConstructor
public class TiFallbackFactory<T> implements FallbackFactory<T> {
    private final Target<T> target;

    @Override
    @SuppressWarnings("unchecked")
    public T create(Throwable cause) {
        Class<T> targetType = target.type();
        String targetName = target.name();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetType);
        enhancer.setUseCache(true);
        enhancer.setCallback(new TiFeignFallback<>(targetType, targetName, cause));
        return (T) enhancer.create();
    }
}
