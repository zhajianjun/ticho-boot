package com.ticho.boot.log;

import com.yomahub.tlog.okhttp.TLogOkHttpInterceptor;
import okhttp3.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 *
 * @author zhajianjun
 * @date 2022-11-01 14:46
 */
@Component
public class LogConfig {

    @Bean
    public Interceptor tLogOkHttpInterceptor() {
        return new TLogOkHttpInterceptor();
    }

}
