package com.ticho.boot.log.event;

import com.ticho.boot.log.interceptor.LogInfo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * 接口日志事件
 *
 * @author zhajianjun
 * @date 2023-02-14 16:23
 */
@Slf4j
@Getter
public class LogInfoEvent extends ApplicationContextEvent {

    private final LogInfo logInfo;

    public LogInfoEvent(ApplicationContext source, LogInfo logInfo) {
        super(source);
        this.logInfo = logInfo;
    }

}
