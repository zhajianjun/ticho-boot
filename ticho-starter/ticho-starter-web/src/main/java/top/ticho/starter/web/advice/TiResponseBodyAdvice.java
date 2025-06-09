package top.ticho.starter.web.advice;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.enums.TiErrorCode;
import top.ticho.starter.view.enums.TiHttpErrorCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.exception.TiSysException;
import top.ticho.starter.web.annotation.TiView;

import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * 基础全局异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class TiResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    public static Map<Class<? extends Throwable>, TiHttpErrorCode> errCodeMap = null;
    private final HttpServletResponse response;

    static {
        TiResponseBodyAdvice.errCodeMap = Map.ofEntries(
            Map.entry(BindException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(TypeMismatchException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(NoHandlerFoundException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(HandlerMethodValidationException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(ServletRequestBindingException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(HttpMessageNotReadableException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(MissingServletRequestPartException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(MissingServletRequestParameterException.class, TiHttpErrorCode.BAD_REQUEST),
            Map.entry(MissingPathVariableException.class, TiHttpErrorCode.INTERNAL_SERVER_ERROR),
            Map.entry(ConversionNotSupportedException.class, TiHttpErrorCode.INTERNAL_SERVER_ERROR),
            Map.entry(HttpMessageNotWritableException.class, TiHttpErrorCode.INTERNAL_SERVER_ERROR),
            Map.entry(HttpRequestMethodNotSupportedException.class, TiHttpErrorCode.METHOD_NOT_ALLOWED),
            Map.entry(HttpMediaTypeNotSupportedException.class, TiHttpErrorCode.UNSUPPORTED_MEDIA_TYPE),
            Map.entry(HttpMediaTypeNotAcceptableException.class, TiHttpErrorCode.NOT_ACCEPTABLE),
            Map.entry(AsyncRequestTimeoutException.class, TiHttpErrorCode.SERVICE_UNAVAILABLE)
        );
    }

    /**
     * 全局错误用于捕获不可预知的异常
     */
    @ExceptionHandler(Exception.class)
    public TiResult<String> exception(Exception ex) {
        if (ex instanceof TiBizException tiBizException) {
            // 业务异常
            response.setStatus(HttpStatus.OK.value());
            log.warn("catch error\t{}", ex.getMessage());
            return TiResult.of(tiBizException.getCode(), tiBizException.getMessage());
        }
        TiErrorCode errCode = errCodeMap.get(ex.getClass());
        TiResult<String> tiResult;
        if (errCode != null) {
            tiResult = TiResult.of(errCode);
            response.setStatus(tiResult.getCode());
        } else if (ex instanceof TiSysException systemException) {
            // 系统异常
            tiResult = TiResult.of(systemException.getCode(), systemException.getMessage());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else {
            // 未知异常
            tiResult = TiResult.of(TiHttpErrorCode.FAIL);
            tiResult.setMessage(ex.getMessage());
            response.setStatus(tiResult.getCode());
        }
        log.error("catch error\t{}", ex.getMessage(), ex);
        return tiResult;
    }


    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        Class<?> declaringClass = methodParameter.getDeclaringClass();
        TiView tiViewOfClass = declaringClass.getDeclaredAnnotation(TiView.class);
        TiView tiViewOfMethod = methodParameter.getMethodAnnotation(TiView.class);
        // 类上是否加了注解，且ignore = false
        // 仅方法上有注解，或者该注解 ignore=false
        if (tiViewOfClass != null && !tiViewOfClass.ignore()) {
            // 方法上没有注解，或者该注解 ignore=false
            return tiViewOfMethod == null || !tiViewOfMethod.ignore();
        }
        return tiViewOfMethod != null && !tiViewOfMethod.ignore();
    }

    @Override
    public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class<? extends HttpMessageConverter<?>> aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
        if (o instanceof TiResult) {
            return o;
        }
        return TiResult.ok(o);
    }

}
