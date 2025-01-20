package top.ticho.boot.http.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import top.ticho.boot.view.log.TiHttpLog;

/**
 * http接口调用日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class HttpLogEvent extends ApplicationContextEvent {

    private final TiHttpLog TIHttpLog;

    public HttpLogEvent(ApplicationContext source, TiHttpLog TIHttpLog) {
        super(source);
        this.TIHttpLog = TIHttpLog;
    }

}
