package com.liu.Controller;

import com.liu.Consumer.Receiver;
import com.liu.Product.Sender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class TestController {

    @Autowired
    private Sender send;

    @Autowired
    private Receiver receiver;

    /**
     * 发送测试消息队列
     */
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String add() {
        send.send();
        return "success";
    }

    /**
     * 发送测试消息队列
     */
    @RequestMapping(value = "/comsumer", method = RequestMethod.GET)
    public String consuner() {
        receiver.process("111");
        return "success";
    }

}
