package com.liu.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author: bin.liu
 * @create: 2018-03-01 16:04
 **/
@Component
@RabbitListener(queues = "topic.messages")
public class topicMessagesReceiver {
    @RabbitHandler
    public void process(String msg) {
        System.out.println("topicMessagesReceiver  : " +msg);
    }
}
