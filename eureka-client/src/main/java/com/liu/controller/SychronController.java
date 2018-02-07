package com.liu.controller;

import com.liu.service.SychronDataService;
import com.liu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
public class SychronController {

    @Autowired
    private SychronDataService sychronDataService;

    @RequestMapping(value = "/sychron/{industry}/{startTime}/{endTime}", method = RequestMethod.GET)
    public void sychron(@PathVariable String industry, @PathVariable String startTime, @PathVariable String endTime) throws ParseException {
        sychronDataService.sychron(startTime,endTime,industry);
    }

    @RequestMapping(value = "/permission", method = RequestMethod.GET)
    public Object sychronPermission() throws ParseException {
        sychronDataService.permission();
        return  ResultUtil.success(null);
    }
}
