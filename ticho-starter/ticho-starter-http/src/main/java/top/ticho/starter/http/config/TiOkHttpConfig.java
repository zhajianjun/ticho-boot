package top.ticho.starter.http.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import top.ticho.starter.http.interceptor.TiOkHttpInterceptor;
import top.ticho.starter.http.prop.TiHttpProperty;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * RestTemplate配置
 *
 * @author zhajianjun
 * @date 2021-10-28 23:43
 */
@Configuration
@Slf4j
@PropertySource(value = "classpath:ticho-http.properties")
public class TiOkHttpConfig {

    /**
     * 用于修改okhttp
     */
    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConfigurationProperties(prefix = "ticho.http")
    public TiHttpProperty tichoFeignProperty() {
        return new TiHttpProperty();
    }

    @Bean
    @ConditionalOnMissingBean(Interceptor.class)
    public Interceptor defaultInterceptor() {
        return chain -> {
            Request req = chain.request();
            return chain.proceed(req);
        };
    }

    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({OkHttpClient.class})
    public OkHttpClient okHttpClient(TiHttpProperty prop, List<Interceptor> interceptors) {
        OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        interceptors.forEach(builder::addInterceptor);
        if (Boolean.TRUE.equals(prop.getOpenLog())) {
            builder.addInterceptor(new TiOkHttpInterceptor(prop));
        }
        return builder
            // 设置连接超时
            .connectTimeout(prop.getConnectTimeout(), TimeUnit.SECONDS)
            // 设置读超时
            .readTimeout(prop.getConnectTimeout(), TimeUnit.SECONDS)
            // 设置写超时
            .writeTimeout(prop.getWriteTimeout(), TimeUnit.SECONDS)
            // 是否自动重连
            .retryOnConnectionFailure(prop.getRetryOnConnectionFailure())
            .connectionPool(new ConnectionPool(prop.getMaxIdleConnections(), prop.getKeepAliveDuration(), TimeUnit.MINUTES))
            // 信任所有证书
            .sslSocketFactory(createSSLSocketFactory(), new TrustAllCerts())
            // 主机名验证，信任所有
            .hostnameVerifier(new TrustAllHostnameVerifier())
            .build();
    }

    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({OkHttp3ClientHttpRequestFactory.class})
    public OkHttp3ClientHttpRequestFactory httpRequestFactory(OkHttpClient okHttpClient) {
        return new OkHttp3ClientHttpRequestFactory(okHttpClient);
    }

    /**
     * 基于OkHttp3配置RestTemplate
     */
    @Bean
    @ConditionalOnProperty(value = "ticho.http.enable", havingValue = "true", matchIfMissing = true)
    @ConditionalOnMissingBean({RestTemplate.class})
    public RestTemplate restTemplate(OkHttp3ClientHttpRequestFactory httpRequestFactory) {
        return new RestTemplate(httpRequestFactory);
    }


    private static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
        }
        return ssfFactory;
    }

    private static class TrustAllCerts implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

}
