package top.ticho.boot.web.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * 程序启动成功实现
 *
 * @author zhajianjun
 * @date 2022-07-10 15:56:30
 * @see ApplicationReadyEvent 应用程序已准备好执行的事件
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TiApplicationReadyEvent implements ApplicationListener<ApplicationReadyEvent> {

    public static final String SPRING_APPLICATION_NAME_KEY = "spring.application.name";
    public static final String SERVER_PORT = "server.port";

    /**
     * 默认事件
     *
     * @param event ApplicationReadyEvent
     */
    @Override
    @Async
    public void onApplicationEvent(@NonNull ApplicationReadyEvent event) {
        ConfigurableApplicationContext applicationContext = event.getApplicationContext();
        Environment env = applicationContext.getEnvironment();
        String application = env.getProperty(SPRING_APPLICATION_NAME_KEY, "application");
        String port = env.getProperty(SERVER_PORT);
        log.info("{}:{} is ready", application, port);
    }
}
