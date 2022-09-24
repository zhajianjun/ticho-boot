package com.ticho.boot.security.filter;

import cn.hutool.core.util.StrUtil;
import com.ticho.boot.security.constant.SecurityConst;
import com.ticho.boot.security.handle.jwt.JwtDecode;
import com.ticho.boot.security.prop.TichoSecurityProperty;
import com.ticho.boot.view.core.BizErrCode;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.Result;
import com.ticho.boot.view.exception.BizException;
import com.ticho.boot.view.util.Assert;
import com.ticho.boot.web.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * token验证过滤器抽象
 *
 * @author zhajianjun
 * @date 2022-09-22 08:38:17
 */
@Slf4j
public abstract class AbstractAuthTokenFilter<T extends UserDetails> extends OncePerRequestFilter {

    @Autowired
    private JwtDecode jwtDecode;

    @Autowired
    private UserDetailsChecker userDetailsChecker;

    @Autowired
    private TichoSecurityProperty tichoSecurityProperty;

    protected abstract T convert(Map<String, Object> decodeAndVerify);

    // @formatter:off
    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain chain
    ) throws IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        try {
            List<AntPathRequestMatcher> antPathRequestMatchers = tichoSecurityProperty.getAntPathRequestMatchers();
            boolean match = antPathRequestMatchers.stream().anyMatch(x-> x.matches(request));
            if (match || StrUtil.isBlank(token)) {
                chain.doFilter(request, response);
                return;
            }
            token = StrUtil.removePrefixIgnoreCase(token, SecurityConst.BEARER);
            token = StrUtil.trimStart(token);
            Map<String, Object> decodeAndVerify = jwtDecode.decodeAndVerify(token);
            Object type = decodeAndVerify.getOrDefault(SecurityConst.TYPE, "");
            Assert.isTrue(Objects.equals(type, SecurityConst.ACCESS_TOKEN), BizErrCode.FAIL, "token不合法");
            T userDetails = convert(decodeAndVerify);
            Assert.isNotNull(userDetails, BizErrCode.FAIL, "token不合法");
            if (userDetails != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                userDetailsChecker.check(userDetails);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "N/A", userDetails.getAuthorities());
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
            Result<String> retResult = Result.of(HttpErrCode.TOKEN_INVALID, message, request.getRequestURI());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.getWriter().write(JsonUtil.toJsonString(retResult));
        }
    }

}
