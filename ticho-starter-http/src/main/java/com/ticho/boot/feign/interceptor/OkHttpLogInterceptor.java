package com.ticho.boot.feign.interceptor;

import com.ticho.boot.feign.prop.TichoFeignProperty;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpLogInterceptor implements Interceptor {

    private final TichoFeignProperty tichoFeignProperty;

    public OkHttpLogInterceptor(TichoFeignProperty tichoFeignProperty) {
        this.tichoFeignProperty = tichoFeignProperty;
    }

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        // @formatter:off
        Request req = chain.request();
        long t1 = System.currentTimeMillis();
        String reqBody = Optional.ofNullable(req.body()).map(Object::toString).orElse("body is empty");
        Response res = chain.proceed(req);
        long t2 = System.currentTimeMillis();
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        Headers headers = req.headers();
        StringBuilder builder = new StringBuilder();
        Map<String,List<String>> headersMap = headers.toMultimap();
        headersMap.forEach((k,v) -> builder.append("\n    ").append(k).append(": ").append(String.join(",", v)));
        log("\n[------http 请求开始------]\n1.地址:{} {}\n2.请求体:{}\n3.请求头:{}\n4.响应体:{}\n5.响应时间:{}ms\n[------http 请求结束------]",
            req.method(),
            req.url(),
            reqBody,
            builder,
            resBody,
            (t2 - t1)
        );
        return res;
        // @formatter:on
    }

    public void log(String format, Object... arguments) {
        TichoFeignProperty.Level level = tichoFeignProperty.getLevel();
        if (level.compareTo(TichoFeignProperty.Level.INFO) == 0 && log.isInfoEnabled()) {
            log.info(format, arguments);
            return;
        }
        if (level.compareTo(TichoFeignProperty.Level.WARN) == 0 && log.isWarnEnabled()) {
            log.warn(format, arguments);
            return;
        }
        if (log.isDebugEnabled()) {
            log.debug(format, arguments);
        }

    }

}
