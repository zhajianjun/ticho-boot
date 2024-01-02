package top.ticho.boot.http.fallback;

import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.core.Result;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

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

    // @formatter:off

    @Nullable
    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) {
        String errorMessage = cause.getMessage();
        log.error("FeignFallback:[{}.{}] serviceId:[{}] message:[{}]", targetType.getName(), method.getName(), targetName, errorMessage);
        Class<?> returnType = method.getReturnType();
        // 暂时不支持 flux，rx，异步等，返回值不是 R，直接返回 null。
        if (Result.class != returnType) {
            return null;
        }
        // 非 FeignException
        if (!(cause instanceof FeignException)) {
            return Result.of(BizErrCode.APP_SERVICE_ERR, errorMessage);
        }
        FeignException exception = (FeignException) cause;
        String content = exception.contentUTF8();
        // 如果返回的数据为空
        if (ObjectUtils.isEmpty(content)) {
            return Result.of(BizErrCode.APP_SERVICE_ERR, errorMessage);
        }
        return Result.fail(content);
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
