package com.fyb.rabbit.producer.broker;

import com.fyb.rabbit.api.Message;
import com.fyb.rabbit.api.MessageProducer;
import com.fyb.rabbit.api.SendCallback;
import com.fyb.rabbit.api.exception.MessageRunTimeException;

import java.util.List;

/**
 * $ProducerClient 发送消息的实际实现类
 */

public class ProducerClient implements MessageProducer {

    @Override
    public void send(Message message, SendCallback sendCallback) throws MessageRunTimeException {

    }

    @Override
    public void send(Message message) throws MessageRunTimeException {

    }

    @Override
    public void send(List<Message> messages) throws MessageRunTimeException {

    }
}
