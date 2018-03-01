package com.liu.productor;

import com.liu.entity.User;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 用户生产者
 *
 * @author: bin.liu
 * @create: 2018-03-01 15:24
 **/
@Component
public class UserSender {

    @Autowired
    private AmqpTemplate rabbitTemplate;
    public void send() {
        User user = new User("张三","123456");
        System.out.println("UserSender : " + user);
        this.rabbitTemplate.convertAndSend("userQueue", user);
    }
}
