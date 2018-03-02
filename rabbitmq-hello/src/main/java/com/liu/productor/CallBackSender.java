package com.liu.productor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * @author: bin.liu
 * @create: 2018-03-01 16:22
 **/
@Component
public class CallBackSender implements RabbitTemplate.ConfirmCallback {

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        System.out.println("回调确认信息: " + correlationData.getId());
    }

    @Autowired
    private RabbitTemplate rabbitTemplatenew;
    public void send() {
        rabbitTemplatenew.setConfirmCallback(this);
        String msg="callbackSender : 回调生产者生产的一条消息";
        System.out.println(msg );
        //回调信息
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        System.out.println("回调生产者的uuid: " + correlationData.getId());
        this.rabbitTemplatenew.convertAndSend("exchange", "topic.messages", msg, correlationData);
    }

}
