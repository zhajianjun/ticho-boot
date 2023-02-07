package com.ticho.boot.rabbitmq.config;

import com.ticho.boot.rabbitmq.event.MqSendSuccessEvent;
import com.ticho.boot.rabbitmq.event.MqSendToExchangeFailEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.lang.NonNull;

import javax.annotation.PostConstruct;

/**
 * rabbitmq 配置
 *
 * @author zhajianjun
 * @date 2022-09-13 16:39:23
 */
@Configuration
@PropertySource(value = "classpath:ticho-rabbitmq.properties")
@Slf4j
public class BaseRabbitmqConfig implements RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {


    // @formatter:off

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }


    /**
     * 发送到路由失败回调，无论是否失败都会触发
     *
     * @param correlationData correlationData
     * @param ack ack
     * @param cause cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        Message returnedMessage = null;
        String id = null;
        if(correlationData != null){
            id = correlationData.getId();
            returnedMessage = correlationData.getReturnedMessage();
        }
        if (ack) {
            log.debug("消息发送到交换机成功,correlationDataId={}", id);
            applicationContext.publishEvent(new MqSendSuccessEvent(applicationContext, returnedMessage));
        } else {
            log.error("消息发送到交换机失败,correlationDataId={}，原因：{}", id, cause);
            applicationContext.publishEvent(new MqSendToExchangeFailEvent(applicationContext, returnedMessage));
        }
    }

    /**
     * 路由发送到队列失败回调，只有失败才会触发
     *
     * @param message message
     * @param replyCode replyCode
     * @param replyText replyText
     * @param exchange exchange
     * @param routingKey routingKey
     */
    @Override
    public void returnedMessage(
        @NonNull Message message,
        int replyCode,
        @NonNull String replyText,
        @NonNull String exchange,
        @NonNull String routingKey
    ) {
        log.error("发送到队列失败，replyCode={},replyText={}", replyCode, replyText);
        applicationContext.publishEvent(new MqSendToExchangeFailEvent(applicationContext, message));
    }
}
