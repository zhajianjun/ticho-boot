package com.ticho.boot.log.event;

import com.ticho.boot.view.log.HttpLog;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 服务接口调用日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class WebLogEvent extends ApplicationContextEvent {

    private final HttpLog httpLog;

    public WebLogEvent(ApplicationContext source, HttpLog httpLog) {
        super(source);
        this.httpLog = httpLog;
    }

}
