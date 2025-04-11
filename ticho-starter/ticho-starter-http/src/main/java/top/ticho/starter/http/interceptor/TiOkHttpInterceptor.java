package top.ticho.starter.http.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.ticho.starter.http.event.TiHttpLogEvent;
import top.ticho.starter.http.prop.TiHttpProperty;
import top.ticho.starter.view.log.TiHttpLog;
import top.ticho.tool.json.util.TiJsonUtil;

import java.io.IOException;
import java.net.URI;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
@RequiredArgsConstructor
public class TiOkHttpInterceptor implements Interceptor {
    /** 日志配置 */
    private final TiHttpProperty tiHttpProperty;

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        // 如果不打印日志，则直接返回
        if (!Boolean.TRUE.equals(tiHttpProperty.getPrintLog())) {
            return chain.proceed(req);
        }
        long t1 = System.currentTimeMillis();
        String reqBody = getReqBody(req);
        Map<String, Object> reqHeaderMap = getHeaderMap(req.headers());
        String reqHeaders = toJson(reqHeaderMap);
        String method = req.method();
        HttpUrl httpUrl = req.url();
        String fullUrl = StrUtil.subBefore(httpUrl.toString(), "?", false);
        Map<String, Object> paramsMap = getParams(httpUrl);
        String params = toJson(paramsMap);
        log.info("[HTTP] {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", method, fullUrl, nullOfDefault(params), nullOfDefault(reqBody), nullOfDefault(reqHeaders));
        Response res = chain.proceed(req);
        Map<String, Object> resHeaderMap = getHeaderMap(res.headers());
        String resHeader = toJson(resHeaderMap);
        long t2 = System.currentTimeMillis();
        // 这里不能直接使用response.body().string()的方式输出日志
        // 因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        // 个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        int status = res.code();
        long millis = t2 - t1;
        log.info("[HTTP] {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}, 响应头={}", method, fullUrl, status, millis, resBody, resHeader);
        URI uri = URLUtil.toURI(fullUrl);
        String host = uri.getHost();
        String port = Integer.toString(uri.getPort());
        String url = uri.getPath();
        TiHttpLog TIHttpLog = TiHttpLog.builder()
            .type(method)
            .ip(host)
            .url(url)
            .port(port)
            .fullUrl(url)
            .reqParams(params)
            .reqBody(reqBody)
            .reqHeaders(reqHeaders)
            .resBody(resBody)
            .start(t1)
            .end(t2)
            .consume(millis)
            .status(status)
            .username(getUsername())
            .mdcMap(MDC.getCopyOfContextMap())
            .build();
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        applicationContext.publishEvent(new TiHttpLogEvent(applicationContext, TIHttpLog));
        return res;
    }

    private Map<String, Object> getHeaderMap(Headers headers) {
        Map<String, List<String>> headersGroupMap = headers.toMultimap();
        Map<String, Object> headersMap = new HashMap<>();
        headersGroupMap.forEach((k, v) -> headersMap.put(k, String.join(",", v)));
        return headersMap;
    }

    public String getUsername() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return Optional.ofNullable(request.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
    }

    public Map<String, Object> getParams(HttpUrl httpUrl) {
        Map<String, Object> map = new HashMap<>();
        Set<String> parameterNames = httpUrl.queryParameterNames();
        for (String parameterName : parameterNames) {
            List<String> values = httpUrl.queryParameterValues(parameterName);
            map.put(parameterName, values.stream().filter(StrUtil::isNotBlank).collect(Collectors.joining(",")));
        }
        return map;
    }

    private String getReqBody(Request req) {
        RequestBody body = req.body();
        if (body == null) {
            return null;
        }
        try (Buffer buffer = new Buffer()) {
            body.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    private String toJson(Map<String, Object> map) {
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        return TiJsonUtil.toJsonString(map);
    }

    private String nullOfDefault(String result) {
        if (result == null) {
            return StrUtil.EMPTY;
        }
        return result;
    }

}
