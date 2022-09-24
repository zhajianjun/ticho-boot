package com.ticho.boot.security.view;

import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.Result;
import com.ticho.boot.web.util.JsonUtil;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 无认证信息返回视图
 *
 * <p>
 *     未放行的接口header中没有token信息时，异常返回视图
 * </p>
 *
 * @author zhajianjun
 * @date 2022-09-23 17:44:39
 */
public class NoAuthenticationMessageView implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
        Result<String> result = Result.of(HttpErrCode.NOT_LOGIN);
        result.setData(req.getRequestURI());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = res.getWriter();
        writer.write(JsonUtil.toJsonString(result));
        writer.close();
    }
}
