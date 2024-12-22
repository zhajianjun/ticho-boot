package top.ticho.boot.http.fallback;

import cn.hutool.core.util.StrUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;
import top.ticho.boot.view.core.TiResult;
import top.ticho.boot.view.enums.TiBizErrCode;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * fallback 代理处理
 *
 * @author zhajianjun
 * @date 2022-08-06 14:10:35
 */
@Slf4j
@AllArgsConstructor
public class DefaultFeignFallback<T> implements MethodInterceptor {

    private final Class<T> targetType;
    private final String targetName;
    private final Throwable cause;


    @Nullable
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        String errorMessage = cause.getMessage();
        log.error("FeignFallback:[{}.{}] serviceId:[{}] message:[{}]", targetType.getName(), method.getName(), targetName, errorMessage);
        Class<?> returnType = method.getReturnType();
        // 暂时不支持 flux，rx，异步等，返回值不是 R，直接返回 null。
        if (TiResult.class != returnType) {
            return null;
        }
        // 非 FeignException
        if (!(cause instanceof FeignException)) {
            return TiResult.of(TiBizErrCode.APP_SERVICE_ERR, errorMessage);
        }
        FeignException exception = (FeignException) cause;
        String content = exception.contentUTF8();
        // 如果返回的数据为空
        if (StrUtil.isBlank(content)) {
            return TiResult.of(TiBizErrCode.APP_SERVICE_ERR, errorMessage);
        }
        return TiResult.fail(content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DefaultFeignFallback<?> that = (DefaultFeignFallback<?>) o;
        return targetType.equals(that.targetType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetType);
    }
}
