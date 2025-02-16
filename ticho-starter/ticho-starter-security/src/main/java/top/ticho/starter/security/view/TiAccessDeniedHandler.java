package top.ticho.starter.security.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.tool.json.util.TiJsonUtil;

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
public class TiAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException e) throws IOException {
        TiResult<String> tiResult = TiResult.of(TiHttpErrCode.TOKEN_INVALID);
        tiResult.setData(req.getRequestURI());
        res.setStatus(tiResult.getCode());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        PrintWriter writer = res.getWriter();
        writer.write(TiJsonUtil.toJsonString(tiResult));
        writer.close();
    }

}
