package com.ticho.boot.feign.log;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Optional;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
public class OkHttpInterceptor implements Interceptor {

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        // @formatter:off
        Request req = chain.request();
        long t1 = System.nanoTime();
        String reqBody = Optional.ofNullable(req.body()).map(Object::toString).orElse("body is empty");
        log.info("[http请求开始] 地址:{} --headers:{} --body:{}", req.url(), req.headers(), reqBody);
        Response res = chain.proceed(req);
        long t2 = System.nanoTime();
        //这里不能直接使用response.body().string()的方式输出日志
        //因为response.body().string()之后，response中的流会被关闭，程序会报错，我们需要创建出一
        //个新的response给应用层处理
        int byteCount = 1024 * 1024;
        ResponseBody body = res.peekBody(byteCount);
        String resBody = body.string();
        log.info("[http请求结束] 地址:{} headers:{} --响应时间:{} --body:{}", req.url(), res.headers(), (t2 - t1) / 1e6d, resBody);
        return res;
        // @formatter:on
    }

}
