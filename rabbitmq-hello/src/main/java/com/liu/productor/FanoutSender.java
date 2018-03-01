package com.liu.productor;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author: bin.liu
 * @create: 2018-03-01 16:12
 **/
@Component
public class FanoutSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;

    public void send() {
        String msgString="我是广播发送的信息";
        System.out.println(msgString);
        this.rabbitTemplate.convertAndSend("fanoutExchange","ssss", msgString);
    }
}
