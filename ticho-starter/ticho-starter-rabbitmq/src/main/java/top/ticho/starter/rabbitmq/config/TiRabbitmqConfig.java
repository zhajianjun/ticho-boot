package top.ticho.starter.rabbitmq.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import top.ticho.starter.rabbitmq.event.MqSendSuccessEvent;
import top.ticho.starter.rabbitmq.event.TiMqSendToExchangeFailEvent;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

/**
 * rabbitmq 配置
 *
 * @author zhajianjun
 * @date 2022-09-13 16:39
 */
@Configuration
@PropertySource(value = "classpath:ticho-rabbitmq.properties")
@Slf4j
public class TiRabbitmqConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnsCallback {


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnsCallback(this);
        rabbitTemplate.setMessageConverter(new JacksonJsonMessageConverter());
    }


    /**
     * 发送到路由失败回调，无论是否失败都会触发
     *
     * @param correlationData correlationData
     * @param ack             ack
     * @param cause           cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        Message returnedMessage = null;
        String id = null;
        Optional<CorrelationData> correlationDataOpt = Optional.ofNullable(correlationData);
        if (correlationDataOpt.isPresent()) {
            id = correlationData.getId();
            returnedMessage = correlationDataOpt
                .map(CorrelationData::getReturned)
                .map(ReturnedMessage::getMessage)
                .orElse(null);
        }
        if (ack) {
            log.debug("消息发送到交换机成功,correlationDataId={}", id);
            applicationContext.publishEvent(new MqSendSuccessEvent(applicationContext, returnedMessage));
        } else {
            log.error("消息发送到交换机失败,correlationDataId={}，原因：{}", id, cause);
            applicationContext.publishEvent(new TiMqSendToExchangeFailEvent(applicationContext, returnedMessage));
        }
    }

    /**
     * 路由发送到队列失败回调，只有失败才会触发
     */
    @Override
    public void returnedMessage(ReturnedMessage returnedMessage) {
        String replyText = returnedMessage.getReplyText();
        int replyCode = returnedMessage.getReplyCode();
        Message message = returnedMessage.getMessage();
        log.error("发送到队列失败，replyCode={},replyText={}", replyCode, replyText);
        applicationContext.publishEvent(new TiMqSendToExchangeFailEvent(applicationContext, message));
    }
}
