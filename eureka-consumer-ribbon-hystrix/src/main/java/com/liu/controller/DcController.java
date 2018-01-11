package com.liu.controller;

import com.liu.service.ConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class DcController {

    @Autowired
    ConsumerService consumerService;

    @GetMapping("/consumer")
    public String dc() {
        return consumerService.consumer();
    }
    @RequestMapping(value= "/index")
    public String index(){
        return  "hello spring boot";
    }
    @RequestMapping("/info")
    public String info(){
        return  "服务消费者";
    }
}