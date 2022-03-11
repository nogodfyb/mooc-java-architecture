package com.example.demo.component;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

/**
 * @author fyb
 * @since 2022/3/11
 */
@Component
public class RabbitReceive {


    //此配置可以直接创建Exchange、Queue、Binding
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(
                    name = "queue-1",
                    durable = "true"
            ),
            exchange = @Exchange(
                    name = "exchange-1",
                    durable = "true",
                    type = "topic",
                    ignoreDeclarationExceptions = "true"
            ),
            key = "springboot.*"
    )
    )
    @RabbitHandler
    public void onMessage(Message message, Channel channel) throws Exception {
        // 1.收到消息以后进行业务端消费处理
        System.err.println("------------------------------------");
        System.err.println("消费端Payload:" + message.getPayload());
        // 2.处理成功之后 获取deliveryTag 并进行手工的ACK操作
        Long deliveryTag = (Long) message.getHeaders().get(AmqpHeaders.DELIVERY_TAG);
        //手工ACK
        channel.basicAck(deliveryTag, false);
    }

}
