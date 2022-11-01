package com.ticho.boot.security.view;

import com.ticho.boot.json.JsonUtil;
import com.ticho.boot.view.core.HttpErrCode;
import com.ticho.boot.view.core.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;


/**
 * 认证成功,权限不足返回视图
 *
 * @author zhajianjun
 * @date 2022-09-23 17:44:39
 */
@Slf4j
public class TichoAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        Result<String> result = Result.of(HttpErrCode.TOKEN_INVALID);
        result.setData(req.getRequestURI());
        res.setStatus(result.getCode());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = res.getWriter();
        writer.write(JsonUtil.toJsonString(result));
        writer.close();
    }

}
