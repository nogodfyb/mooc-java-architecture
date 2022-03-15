package com.example.demo.component;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class RabbitSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    /**
     * 确认消息已到达broker端
     */
    final RabbitTemplate.ConfirmCallback confirmCallback = new RabbitTemplate.ConfirmCallback() {
        @Override
        public void confirm(CorrelationData correlationData, boolean ack, String cause) {

            System.err.println("confirm correlationData:" + correlationData);
            System.err.println("confirm ack:" + ack);

        }
    };


    /**
     * 对外发送消息的方法
     *
     * @param message    具体的消息内容
     * @param properties 额外的附加属性
     * @throws Exception
     */
    public void send(Object message, Map<String, Object> properties) throws Exception {

        MessageHeaders messageHeaders = new MessageHeaders(properties);

        Message<Object> msg = MessageBuilder.createMessage(message, messageHeaders);

        rabbitTemplate.setConfirmCallback(confirmCallback);

        // 指定业务唯一id
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());

        MessagePostProcessor mpp = new MessagePostProcessor() {
            @Override
            public org.springframework.amqp.core.Message postProcessMessage(org.springframework.amqp.core.Message message) throws AmqpException {

                System.err.println("post to do:" + message);

                return message;
            }
        };

        rabbitTemplate.convertAndSend("exchange-1", "springboot.rabbit", msg, mpp, correlationData);

    }


}
