package top.ticho.starter.security.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.ticho.starter.view.core.TiResult;
import top.ticho.tool.core.enums.TiHttpErrorCode;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author zhajianjun
 * @date 2022-09-26 17:49
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public class TiSecurityControllerAdvice {


    /**
     * AccessDeniedException
     *
     * @param e e
     * @return Result
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public TiResult<String> accessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足 e={}", e.getMessage());
        return TiResult.of(TiHttpErrorCode.FORBIDDEN, e.getMessage(), request.getRequestURI());
    }

    /**
     * AuthenticationException
     *
     * @param e e
     * @return Result
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public TiResult<String> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("登录异常 e={}", e.getMessage());
        return TiResult.of(TiHttpErrorCode.NOT_LOGIN, e.getMessage(), request.getRequestURI());
    }

}
