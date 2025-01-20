package top.ticho.starter.security.view;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import top.ticho.starter.view.core.TiResult;
import top.ticho.starter.view.enums.TiHttpErrCode;
import top.ticho.tool.json.util.TiJsonUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 无认证信息返回视图
 *
 * <p>
 * 未放行的接口header中没有token信息时，异常返回视图
 * </p>
 *
 * @author zhajianjun
 * @date 2022-09-23 17:44:39
 */
public class BaseAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException e) throws IOException {
        TiResult<String> tiResult = TiResult.of(TiHttpErrCode.NOT_LOGIN);
        tiResult.setData(req.getRequestURI());
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);
        res.setCharacterEncoding(StandardCharsets.UTF_8.name());
        res.setStatus(tiResult.getCode());
        PrintWriter writer = res.getWriter();
        writer.write(TiJsonUtil.toJsonString(tiResult));
        writer.close();
    }
}
