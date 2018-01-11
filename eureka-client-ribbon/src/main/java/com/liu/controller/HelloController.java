package com.liu.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {

    @ResponseBody
    @RequestMapping("/index")
    public String index(){
        return  "hello spring boot";
    }

    @RequestMapping("/hello")
    public String hello(ModelMap map){
        map.put("name","刘斌");
        return "hello";
    }

    @ResponseBody
    @RequestMapping("/info")
    public String info(){
        return  "服务提供者-->ribbon客户端";
    }
}
