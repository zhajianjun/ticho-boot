package top.ticho.boot.web.handle;

import top.ticho.boot.view.enums.HttpErrCode;
import top.ticho.boot.view.core.Result;
import top.ticho.boot.view.enums.IErrCode;
import top.ticho.boot.view.exception.BizException;
import top.ticho.boot.view.exception.SysException;
import top.ticho.boot.web.annotation.View;
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
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * 基础全局异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class BaseBaseResponseHandle implements ResponseBodyAdvice<Object> {
    private static Map<Class<? extends Throwable>, IErrCode> errCodeMap = null;


    static {
        Map<Class<? extends Throwable>, IErrCode> errCodeMap = new HashMap<>();
        errCodeMap.put(BindException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(TypeMismatchException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(NoHandlerFoundException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(ServletRequestBindingException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(HttpMessageNotReadableException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(MethodArgumentNotValidException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(MissingServletRequestPartException.class, HttpErrCode.BAD_REQUEST);
        errCodeMap.put(MissingServletRequestParameterException.class, HttpErrCode.BAD_REQUEST);

        errCodeMap.put(MissingPathVariableException.class, HttpErrCode.INTERNAL_SERVER_ERROR);
        errCodeMap.put(ConversionNotSupportedException.class, HttpErrCode.INTERNAL_SERVER_ERROR);
        errCodeMap.put(HttpMessageNotWritableException.class, HttpErrCode.INTERNAL_SERVER_ERROR);

        errCodeMap.put(HttpRequestMethodNotSupportedException.class, HttpErrCode.METHOD_NOT_ALLOWED);
        errCodeMap.put(HttpMediaTypeNotSupportedException.class, HttpErrCode.UNSUPPORTED_MEDIA_TYPE);
        errCodeMap.put(HttpMediaTypeNotAcceptableException.class, HttpErrCode.NOT_ACCEPTABLE);

        errCodeMap.put(AsyncRequestTimeoutException.class, HttpErrCode.SERVICE_UNAVAILABLE);
        BaseBaseResponseHandle.errCodeMap = Collections.unmodifiableMap(errCodeMap);
    }

    /**
     * 全局错误用于捕获不可预知的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<String> exception(Exception ex, HttpServletResponse res) {
        if (ex instanceof BizException) {
            // 业务异常
            BizException bizException = (BizException) ex;
            res.setStatus(HttpStatus.OK.value());
            log.warn("catch error\t{}", ex.getMessage());
            return Result.of(bizException.getCode(), bizException.getMsg());
        }

        IErrCode iErrCode = errCodeMap.get(ex.getClass());
        Result<String> result;
        if (iErrCode != null) {
            result = Result.of(iErrCode);
            res.setStatus(result.getCode());
        } else if (ex instanceof SysException) {
            // 系统异常
            SysException systemException = (SysException) ex;
            result = Result.of(systemException.getCode(), systemException.getMsg());
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else {
            // 未知异常
            result = Result.of(HttpErrCode.FAIL);
            result.setMsg(ex.getMessage());
            res.setStatus(result.getCode());
        }
        log.error("catch error\t{}", ex.getMessage(), ex);
        return result;
    }

    // @formatter:off

    @Override
    public boolean supports(@NonNull MethodParameter methodParameter, @NonNull Class<? extends HttpMessageConverter<?>> aClass) {
        Class<?> declaringClass = methodParameter.getDeclaringClass();
        View typeView = declaringClass.getDeclaredAnnotation(View.class);
        View methodView = methodParameter.getMethodAnnotation(View.class);
        // action对应的类上是否加了注解，且ignore = false
        // 仅方法上有注解，或者该注解 ignore=false
        if (typeView != null && !typeView.ignore()) {
            // 方法上没有注解，或者该注解 ignore=false
            return methodView == null || !methodView.ignore();
        } else {
            return methodView != null && !methodView.ignore();
        }
    }

    @Override
    public Object beforeBodyWrite(Object o, @NonNull MethodParameter methodParameter, @NonNull MediaType mediaType, @NonNull Class<? extends HttpMessageConverter<?>> aClass, @NonNull ServerHttpRequest serverHttpRequest, @NonNull ServerHttpResponse serverHttpResponse) {
        if (o instanceof Result) {
            return o;
        }
        return Result.ok(o);
    }
}
