package top.ticho.starter.log.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import top.ticho.starter.view.log.TiHttpLog;

/**
 * 服务接口调用日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class TiWebLogEvent extends ApplicationContextEvent {

    private final TiHttpLog tiHttpLog;
    private final Object handler;

    public TiWebLogEvent(ApplicationContext source, TiHttpLog tiHttpLog, Object handler) {
        super(source);
        this.tiHttpLog = tiHttpLog;
        this.handler = handler;
    }

}
