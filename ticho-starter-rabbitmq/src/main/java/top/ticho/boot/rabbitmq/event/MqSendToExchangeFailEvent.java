package top.ticho.boot.rabbitmq.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * mq发送交换机失败事件
 *
 * @author zhajianjun
 * @date 2022-09-13 16:39:23
 */
@Slf4j
@Getter
public class MqSendToExchangeFailEvent extends ApplicationContextEvent {
    private final Message message;

    public MqSendToExchangeFailEvent(ApplicationContext source, Message message) {
        super(source);
        this.message = message;
    }

}
