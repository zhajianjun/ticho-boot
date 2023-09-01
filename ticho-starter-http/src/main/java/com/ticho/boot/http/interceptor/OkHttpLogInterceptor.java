package com.ticho.boot.http.interceptor;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ticho.boot.http.event.HttpLogEvent;
import com.ticho.boot.http.prop.BaseHttpProperty;
import com.ticho.boot.json.util.JsonUtil;
import com.ticho.boot.view.log.HttpLog;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
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
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    /** 日志配置 */
    private final BaseHttpProperty baseHttpProperty;

    public OkHttpLogInterceptor(BaseHttpProperty baseHttpProperty) {
        this.baseHttpProperty = baseHttpProperty;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        // @formatter:of
        Request req = chain.request();
        // 如果不打印日志，则直接返回
        if (!Boolean.TRUE.equals(baseHttpProperty.getPrintLog())) {
            return chain.proceed(req);
        }
        String reqPrefix = baseHttpProperty.getReqPrefix();
        long t1 = System.currentTimeMillis();
        String reqBody = getReqBody(req);
        Map<String, Object> reqHeaderMap = getHeaderMap(req.headers());
        String reqHeaders = toJson(reqHeaderMap);
        String method = req.method();
        HttpUrl httpUrl = req.url();
        String fullUrl = StrUtil.subBefore(httpUrl.toString(), "?", false);
        Map<String, Object> paramsMap = getParams(httpUrl);
        String params = toJson(paramsMap);
        log.info("{} {} {} 请求开始, 请求参数={}, 请求体={}, 请求头={}", reqPrefix, method, fullUrl, nullOfDefault(params), nullOfDefault(reqBody), nullOfDefault(reqHeaders));
        Response res = chain.proceed(req);
        Map<String, Object> resHeaderMap = getHeaderMap(res.headers());
        String resHeader = toJson(resHeaderMap);
        long t2 = System.currentTimeMillis();
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        int status = res.code();
        long millis = t2 - t1;
        log.info("{} {} {} 请求结束, 状态={}, 耗时={}ms, 响应参数={}, 响应头={}", reqPrefix, method, fullUrl, status, millis, resBody, resHeader);
        URI uri = URLUtil.toURI(fullUrl);
        String host = uri.getHost();
        String port = Integer.toString(uri.getPort());
        String url = uri.getPath();
        HttpLog httpLog = HttpLog.builder()
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
            .build();
        ApplicationContext applicationContext = SpringUtil.getApplicationContext();
        applicationContext.publishEvent(new HttpLogEvent(applicationContext, httpLog));
        return res;
        // @formatter:on
    }

    private Map<String, Object> getHeaderMap(Headers headers) {
        Map<String,List<String>> headersGroupMap = headers.toMultimap();
        Map<String, Object> headersMap = new HashMap<>();
        headersGroupMap.forEach((k,v) -> headersMap.put(k, String.join(",",v)));
        return headersMap;
    }

    public String getUsername() {
        // @formatter:off
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        HttpServletRequest request = requestAttributes.getRequest();
        return Optional.ofNullable(request.getUserPrincipal())
            .map(Principal::getName)
            .orElse(null);
        // @formatter:on
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

    private String toJson(Map<String, ?> map) {
        if (MapUtil.isEmpty(map)) {
            return null;
        }
        return JsonUtil.toJsonString(map);
    }

    private String nullOfDefault(String result) {
        if (result == null) {
            return StrUtil.EMPTY;
        }
        return result;
    }

}
