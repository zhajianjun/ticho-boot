package com.ticho.boot.security.filter;

import cn.hutool.core.util.StrUtil;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.security.auth.AntPatternsAuthHandle;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.Result;
import com.ticho.boot.view.core.TichoSecurityUser;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * token验证过滤器抽象类
 *
 * @author zhajianjun
 * @date 2022-09-22 08:38:17
 */
@Slf4j
public abstract class AbstractAuthTokenFilter<T extends TichoSecurityUser> extends OncePerRequestFilter {

    @Autowired
    private JwtDecode jwtDecode;

    @Autowired
    private AntPatternsAuthHandle antPatternsAuthHandle;

    /**
     * 前置处理
     *
     * @param request request
     * @param response response
     */
    protected void support(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }

    /**
     * 后置处理
     *
     * @param request request
     * @param response response
     */
    protected void complete(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }

    protected abstract T convert(Map<String, Object> decodeAndVerify);

    // @formatter:off
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws IOException {
        try {
            support(request, response);
            if (antPatternsAuthHandle.ignoreAuth(request)) {
                chain.doFilter(request, response);
                return;
            }
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            Assert.isNotNull(token, HttpErrCode.NOT_LOGIN);
            token = StrUtil.removePrefixIgnoreCase(token, SecurityConst.BEARER);
            token = StrUtil.trimStart(token);
            Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(token);
            Object type = decodeAndVerify.getOrDefault(SecurityConst.TYPE, "");
            Assert.isTrue(Objects.equals(type, SecurityConst.ACCESS_TOKEN), BizErrCode.FAIL, "token不合法");
            T securityUser = convert(decodeAndVerify);
            Assert.isNotNull(securityUser, BizErrCode.FAIL, "token不合法");
            if (securityUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                securityUser.setPassword("N/A");
                List<String> authoritieStrs = Optional.ofNullable(securityUser.getRoleIds()).orElseGet(ArrayList::new);
                List<SimpleGrantedAuthority> authorities = authoritieStrs.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, securityUser.getPassword(), authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            String message = e.getMessage();
            log.warn("catch error\t{}", message);
            if (e instanceof BizException) {
                message = ((BizException) e).getMsg();
            }
            Result<String> result = Result.of(HttpErrCode.TOKEN_INVALID, message, request.getRequestURI());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJsonString(result));
        } finally{
            complete(request, response);
        }
    }

}
