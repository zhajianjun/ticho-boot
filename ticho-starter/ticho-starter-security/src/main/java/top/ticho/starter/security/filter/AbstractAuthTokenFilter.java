package top.ticho.starter.security.filter;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import top.ticho.starter.security.auth.AntPatternsAuthHandle;
import top.ticho.starter.security.constant.TiSecurityConst;
import top.ticho.starter.security.handle.jwt.JwtDecode;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.core.TiSecurityUser;
import top.ticho.starter.view.enums.TiBizErrCode;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.starter.view.exception.TiBizException;
import top.ticho.starter.view.util.TiAssert;
import top.ticho.tool.json.util.TiJsonUtil;

import javax.annotation.Resource;
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
public abstract class AbstractAuthTokenFilter<T extends TiSecurityUser> extends OncePerRequestFilter {

    @Resource
    private JwtDecode jwtDecode;

    @Resource
    private AntPatternsAuthHandle antPatternsAuthHandle;

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
            if (antPatternsAuthHandle.ignoreAuth(request)) {
                if (Objects.nonNull(token)) {
                    token = StrUtil.removePrefixIgnoreCase(token, TiSecurityConst.BEARER);
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
            TiAssert.isNotNull(token, TiHttpErrCode.NOT_LOGIN);
            token = StrUtil.removePrefixIgnoreCase(token, TiSecurityConst.BEARER);
            token = StrUtil.trimStart(token);
            Map<String, Object> map = jwtDecode.decodeAndVerify(token);
            Object type = map.getOrDefault(TiSecurityConst.TYPE, "");
            TiAssert.isTrue(Objects.equals(type, TiSecurityConst.ACCESS_TOKEN), TiBizErrCode.FAIL, "token不合法");
            T securityUser = convert(map);
            TiAssert.isNotNull(securityUser, TiBizErrCode.FAIL, "token不合法");
            setAuthentication(request, securityUser, token);
            chain.doFilter(request, response);
        } catch (Exception e) {
            String message = e.getMessage();
            TiHttpErrCode tokenInvalid = TiHttpErrCode.TOKEN_INVALID;
            log.warn("{} {} {} catch error\t{}", request.getMethod(), request.getRequestURI(), tokenInvalid.getCode(), message, e);
            if (e instanceof TiBizException) {
                message = ((TiBizException) e).getMsg();
            }
            TiResult<String> tiResult = TiResult.of(tokenInvalid, message, request.getRequestURI());
            response.setStatus(tokenInvalid.getCode());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(TiJsonUtil.toJsonString(tiResult));
        } finally {
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
