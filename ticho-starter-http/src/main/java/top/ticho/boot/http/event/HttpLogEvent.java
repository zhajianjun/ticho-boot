package top.ticho.boot.http.event;

import top.ticho.boot.view.log.HttpLog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * http接口调用日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class HttpLogEvent extends ApplicationContextEvent {

    private final HttpLog httpLog;

    public HttpLogEvent(ApplicationContext source, HttpLog httpLog) {
        super(source);
        this.httpLog = httpLog;
    }

}
