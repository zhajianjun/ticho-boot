package top.ticho.starter.web.handle;

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
import org.springframework.lang.NonNull;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.exception.TiSysException;
import top.ticho.starter.web.annotation.TiView;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;


/**
 * 基础全局异常处理
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 10)
public class TiResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    public static Map<Class<? extends Throwable>, TiHttpErrCode> errCodeMap = null;
    private final HttpServletResponse response;

    static {
        Map<Class<? extends Throwable>, TiHttpErrCode> errCodeMap = new HashMap<>();
        errCodeMap.put(BindException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(TypeMismatchException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(NoHandlerFoundException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(ServletRequestBindingException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(HttpMessageNotReadableException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(MissingServletRequestPartException.class, TiHttpErrCode.BAD_REQUEST);
        errCodeMap.put(MissingServletRequestParameterException.class, TiHttpErrCode.BAD_REQUEST);

        errCodeMap.put(MissingPathVariableException.class, TiHttpErrCode.INTERNAL_SERVER_ERROR);
        errCodeMap.put(ConversionNotSupportedException.class, TiHttpErrCode.INTERNAL_SERVER_ERROR);
        errCodeMap.put(HttpMessageNotWritableException.class, TiHttpErrCode.INTERNAL_SERVER_ERROR);

        errCodeMap.put(HttpRequestMethodNotSupportedException.class, TiHttpErrCode.METHOD_NOT_ALLOWED);
        errCodeMap.put(HttpMediaTypeNotSupportedException.class, TiHttpErrCode.UNSUPPORTED_MEDIA_TYPE);
        errCodeMap.put(HttpMediaTypeNotAcceptableException.class, TiHttpErrCode.NOT_ACCEPTABLE);

        errCodeMap.put(AsyncRequestTimeoutException.class, TiHttpErrCode.SERVICE_UNAVAILABLE);
        TiResponseBodyAdvice.errCodeMap = Collections.unmodifiableMap(errCodeMap);
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public TiResult<String> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException ex) {
        BindingResult bindingResult = ex.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        List<FieldError> errors = fieldErrors
            .stream()
            .sorted(Comparator.comparing(ObjectError::getObjectName))
            .peek(next -> joiner.add(next.getField() + ":" + next.getDefaultMessage()))
            .collect(Collectors.toList());
        log.warn("catch error\t{}", joiner);
        return TiResult.fail(TiBizErrCode.PARAM_ERROR, errors.get(0).getDefaultMessage());
    }

    /**
     * 参数校验异常处理
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public TiResult<String> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> fieldErrors = ex.getConstraintViolations();
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        List<ConstraintViolation<?>> errors = fieldErrors
            .stream()
            .sorted(Comparator.comparing(ConstraintViolation::getMessage))
            .peek(next -> joiner.add(next.getPropertyPath() + ":" + next.getMessage()))
            .collect(Collectors.toList());
        log.warn("catch error\t{}", joiner);
        return TiResult.fail(TiBizErrCode.PARAM_ERROR, errors.get(0).getMessage());
    }

    /**
     * 全局错误用于捕获不可预知的异常
     */
    @ExceptionHandler(Exception.class)
    public TiResult<String> exception(Exception ex) {
        if (ex instanceof TiBizException) {
            // 业务异常
            TiBizException tiBizException = (TiBizException) ex;
            response.setStatus(HttpStatus.OK.value());
            log.warn("catch error\t{}", ex.getMessage());
            return TiResult.of(tiBizException.getCode(), tiBizException.getMsg());
        }
        TiHttpErrCode tiHttpErrorCode = errCodeMap.get(ex.getClass());
        TiResult<String> tiResult;
        if (tiHttpErrorCode != null) {
            tiResult = TiResult.of(tiHttpErrorCode);
            response.setStatus(tiResult.getCode());
        } else if (ex instanceof TiSysException) {
            // 系统异常
            TiSysException systemException = (TiSysException) ex;
            tiResult = TiResult.of(systemException.getCode(), systemException.getMsg());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        } else {
            // 未知异常
            tiResult = TiResult.of(TiHttpErrCode.FAIL);
            tiResult.setMsg(ex.getMessage());
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
