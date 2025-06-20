package top.ticho.starter.rabbitmq.event;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * mq交换机发送到队列失败事件
 *
 * @author zhajianjun
 * @date 2022-09-13 16:39
 */
@Slf4j
@Getter
public class TiMqSendToQueueFailEvent extends ApplicationContextEvent {
    private final Message message;

    public TiMqSendToQueueFailEvent(ApplicationContext source, Message message) {
        super(source);
        this.message = message;
    }

}
