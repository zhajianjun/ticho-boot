package top.ticho.starter.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ticho.starter.security.auth.TiAntPatternsAuthHandle;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.prop.TiSecurityProperty;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.tool.core.TiBase64Util;
import top.ticho.tool.core.TiStrUtil;
import top.ticho.tool.core.enums.TiHttpErrorCode;
import top.ticho.tool.json.util.TiJsonUtil;
import top.ticho.tool.jwt.TiJwt;
import top.ticho.tool.jwt.TiJwtUtil;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
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
 * @date 2022-09-22 08:38
 */
@Slf4j
public abstract class AbstractAuthTokenFilter<T extends TiSecurityUser> extends OncePerRequestFilter {

    @Resource
    private TiAntPatternsAuthHandle tiAntPatternsAuthHandle;
    @Resource
    private TiSecurityProperty tiSecurityProperty;

    /**
     * 前置处理
     *
     * @param request  request
     * @param response response
     */
    protected void support(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }

    /**
     * 后置处理
     *
     * @param request  request
     * @param response response
     */
    protected void complete(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response) {
    }

    /**
     * map信息转换为用户信息
     *
     * @param decodeAndVerify 解码和验证
     * @return {@link T}
     */
    protected abstract T convert(Map<String, Object> decodeAndVerify);

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws IOException {
        try {
            support(request, response);
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (tiAntPatternsAuthHandle.ignoreAuth(request)) {
                if (TiStrUtil.isNotBlank(token)) {
                    TiJwt tiJwt = parseToken(token);
                    boolean expired = tiJwt.isExpired();
                    T securityUser = null;
                    if (!expired) {
                        securityUser = convert(tiJwt.claims());
                    }
                    setAuthentication(request, securityUser, tiJwt.token());
                }
                chain.doFilter(request, response);
                return;
            }
            if (TiStrUtil.isBlank(token)) {
                unauthorizedResponse(request, response);
                return;
            }
            TiJwt tiJwt = parseToken(token);
            byte[] publicKeyBytes = TiBase64Util.decodeAsBytes(tiSecurityProperty.getPublicKey());
            if (!tiJwt.isValid(publicKeyBytes)) {
                unauthorizedResponse(request, response);
                return;
            }
            Map<String, Object> claims = tiJwt.claims();
            Object type = claims.getOrDefault(TiSecurityConst.TYPE, "");
            if (!Objects.equals(type, TiSecurityConst.ACCESS_TOKEN)) {
                unauthorizedResponse(request, response);
                return;
            }
            if (tiJwt.isExpired()) {
                unauthorizedResponse(request, response);
                return;
            }
            T securityUser = convert(claims);
            setAuthentication(request, securityUser, tiJwt.token());
            chain.doFilter(request, response);
        } catch (Exception e) {
            log.warn("登录异常，{} {} catch error\t{}", request.getMethod(), request.getRequestURI(), e.getMessage(), e);
            systemErrorResponse(request, response);
        } finally {
            complete(request, response);
        }
    }

    public void systemErrorResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        TiHttpErrorCode errorCode = TiHttpErrorCode.INTERNAL_SERVER_ERROR;
        TiResult<String> tiResult = TiResult.of(errorCode, errorCode.getMessage(), request.getRequestURI());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(TiJsonUtil.toJsonString(tiResult));
        }

    }

    public void unauthorizedResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        TiHttpErrorCode errorCode = TiHttpErrorCode.NOT_LOGIN;
        TiResult<String> tiResult = TiResult.of(errorCode, errorCode.getMessage(), request.getRequestURI());
        try (PrintWriter writer = response.getWriter()) {
            writer.write(TiJsonUtil.toJsonString(tiResult));
        }
    }

    private static TiJwt parseToken(String token) {
        String token0 = TiStrUtil.removePrefixIgnoreCase(token, TiSecurityConst.BEARER);
        String token1 = TiStrUtil.trim(token0);
        return TiJwtUtil.parseToken(token1);
    }

    private void setAuthentication(HttpServletRequest request, T securityUser, String token) {
        if (securityUser != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            securityUser.setPassword("N/A");
            securityUser.setToken(token);
            List<String> authoritieStrs = Optional.ofNullable(securityUser.getRoles()).orElseGet(ArrayList::new);
            List<SimpleGrantedAuthority> authorities = authoritieStrs.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(securityUser, securityUser.getPassword(), authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }

}
