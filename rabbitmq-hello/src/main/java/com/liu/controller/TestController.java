package com.liu.controller;

import com.liu.consumer.ReceiverA;
import com.liu.productor.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    @Autowired
    private SenderA sendA;

    @Autowired
    private SenderB sendB;

    @Autowired
    private UserSender userSender;

    @Autowired
    private TopicSender topicSender;

    @Autowired
    private FanoutSender fanoutSender;

    @Autowired
    private CallBackSender callBackSender;



    /**
     * 发送测试消息队列 生产者多消费者
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        sendA.send("hello");
        return "success";
    }


    @GetMapping ("/oneToMany")
    public void oneToMany() {
        for(int i=0;i<10;i++){
            sendA.send("hello:"+i);
        }
    }

    @GetMapping ("/manyToMany")
    public void manyToMany() {
        for(int i=0;i<10;i++){
            sendA.send("hello:"+i);
            sendB.send("hello:"+i);
        }
    }
    @GetMapping ("/user")
    public void produceUser(){
        userSender.send();
    }

    /**
     * topic exchange类型rabbitmq测试
     */
    @GetMapping("/topic")
    public void topicTest() {
        topicSender.send();
    }

    /**
     * fanout exchange类型rabbitmq测试
     */
    @GetMapping("/fanout")
    public void fanoutTest() {
        fanoutSender.send();
    }

    @GetMapping("/callback")
    public void callbak() {
        callBackSender.send();
    }
}
