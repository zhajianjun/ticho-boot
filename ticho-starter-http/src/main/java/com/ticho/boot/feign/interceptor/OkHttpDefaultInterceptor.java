package com.ticho.boot.feign.interceptor;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Slf4j
@Component
public class OkHttpDefaultInterceptor implements Interceptor {

    @Override
    @NonNull
    public Response intercept(Chain chain) throws IOException {
        Request req = chain.request();
        return chain.proceed(req);
    }

}
