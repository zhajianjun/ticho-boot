package top.ticho.boot.log.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;
import top.ticho.boot.view.log.TiHttpLog;

/**
 * 服务接口调用日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class WebLogEvent extends ApplicationContextEvent {

    private final TiHttpLog tiHttpLog;

    public WebLogEvent(ApplicationContext source, TiHttpLog tiHttpLog) {
        super(source);
        this.tiHttpLog = tiHttpLog;
    }

}
