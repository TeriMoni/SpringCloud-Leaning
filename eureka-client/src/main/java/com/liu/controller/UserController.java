package com.liu.controller;

import com.liu.entity.JmlUser;
import com.liu.exception.ValidateException;
import com.liu.model.User;
import com.liu.pojo.Result;
import com.liu.repository.UserRepository;
import com.liu.service.UserService;
import com.liu.utils.ResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RequestMapping(value = "/user")
@RestController
public class UserController extends BaseController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/add")
    public Object addUser(@Valid JmlUser user , BindingResult bindingResult){
        Result result = new Result();
        if(bindingResult.hasErrors()){
            return ResultUtil.error(1,bindingResult.getFieldError().getDefaultMessage());
        }
        return ResultUtil.success(userRepository.save(user));
    }

    @RequestMapping(value = "/all/{pageNum}/{pageSize}")
    public List<User> findAllUser(@PathVariable("pageNum") int pageNum, @PathVariable("pageSize") int pageSize){
        return userService.findAllUser(pageNum,pageSize);
    }

    @RequestMapping(value = "/find/{id}" )
    public JmlUser selectById(@PathVariable("id") int id){
        return userRepository.findOne(id);
    }


    @RequestMapping(value = "/findByName" )
    public String selectByName(String name,int age){
       return "1111";
    }


    @RequestMapping(value = "/register")
    public void register(@Valid JmlUser user, BindingResult bindingResult) throws Exception{
        if(bindingResult.hasErrors()){
            throw new ValidateException(ResultUtil.error(1001,bindingResult.getFieldError().getDefaultMessage()));
        }
        userService.register(user);
    }
}
