package com.liu.controller;

import com.liu.service.AppInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ModifyAppController {
    private static final Logger logger = LoggerFactory.getLogger(ModifyAppController.class);

    @Autowired
    AppInfoService appInfoService;
    @RequestMapping("test")
    public void test(){
        logger.info("校验图片信息开始");
        appInfoService.analysis();
        logger.info("校验图片信息结束");
    }

}
