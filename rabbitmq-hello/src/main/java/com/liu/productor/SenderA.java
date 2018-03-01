package com.liu.productor;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SenderA {

    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void send(String message) {
        String context = message + new Date();
        System.out.println("SenderA : " + context);
        this.rabbitTemplate.convertAndSend("hello", context);
    }

}
