package com.ticho.boot.security.handle;

import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 *
 * @author zhajianjun
 * @date 2022-09-26 17:49
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE - 20)
public class TichoSecurityControllerAdvice {


    /**
     * AccessDeniedException
     *
     * @param e e
     * @return Result
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> accessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足 e={}", e.getMessage());
        return Result.of(HttpErrCode.ACCESS_DENIED, e.getMessage(), request.getRequestURI());
    }

    /**
     * AuthenticationException
     *
     * @param e e
     * @return Result
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<String> authenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("登录异常 e={}", e.getMessage());
        return Result.of(HttpErrCode.NOT_LOGIN, e.getMessage(), request.getRequestURI());
    }

}
