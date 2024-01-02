package top.ticho.boot.security.filter;

import cn.hutool.core.util.StrUtil;
import top.ticho.boot.json.util.JsonUtil;
import top.ticho.boot.security.auth.AntPatternsAuthHandle;
import top.ticho.boot.security.constant.BaseSecurityConst;
import top.ticho.boot.security.handle.jwt.JwtDecode;
import top.ticho.boot.view.enums.BizErrCode;
import top.ticho.boot.view.enums.HttpErrCode;
import top.ticho.boot.view.core.Result;
import top.ticho.boot.view.core.BaseSecurityUser;
import top.ticho.boot.view.exception.BizException;
import top.ticho.boot.view.util.Assert;
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
public abstract class AbstractAuthTokenFilter<T extends BaseSecurityUser> extends OncePerRequestFilter {

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

    /**
     * map信息转换为用户信息
     *
     * @param decodeAndVerify 解码和验证
     * @return {@link T}
     */
    protected abstract T convert(Map<String, Object> decodeAndVerify);

    // @formatter:on
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws IOException {
        try {
            support(request, response);
            String token = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (antPatternsAuthHandle.ignoreAuth(request)) {
                if (Objects.nonNull(token)) {
                    token = StrUtil.removePrefixIgnoreCase(token, BaseSecurityConst.BEARER);
                    token = StrUtil.trimStart(token);
                    Map<String, Object> map = jwtDecode.decode(token);
                    boolean expired = jwtDecode.isExpired(map);
                    T securityUser = null;
                    if (!expired) {
                        securityUser = convert(map);
                    }
                    setAuthentication(request, securityUser, token);
                }
                chain.doFilter(request, response);
                return;
            }
            Assert.isNotNull(token, HttpErrCode.NOT_LOGIN);
            token = StrUtil.removePrefixIgnoreCase(token, BaseSecurityConst.BEARER);
            token = StrUtil.trimStart(token);
            Map<String, Object> map = jwtDecode.decodeAndVerify(token);
            Object type = map.getOrDefault(BaseSecurityConst.TYPE, "");
            Assert.isTrue(Objects.equals(type, BaseSecurityConst.ACCESS_TOKEN), BizErrCode.FAIL, "token不合法");
            T securityUser = convert(map);
            Assert.isNotNull(securityUser, BizErrCode.FAIL, "token不合法");
            setAuthentication(request, securityUser, token);
            chain.doFilter(request, response);
        } catch (Exception e) {
            String message = e.getMessage();
            HttpErrCode tokenInvalid = HttpErrCode.TOKEN_INVALID;
            log.warn("{} {} {} catch error\t{}", request.getMethod(), request.getRequestURI(), tokenInvalid.getCode(), message);
            if (e instanceof BizException) {
                message = ((BizException) e).getMsg();
            }
            Result<String> result = Result.of(tokenInvalid, message, request.getRequestURI());
            response.setStatus(tokenInvalid.getCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJsonString(result));
        } finally{
            complete(request, response);
        }
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
