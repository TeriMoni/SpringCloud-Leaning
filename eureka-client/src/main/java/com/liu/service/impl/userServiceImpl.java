package com.liu.service.impl;

import com.github.pagehelper.PageHelper;
import com.liu.emum.ResultEmum;
import com.liu.entity.JmlUser;
import com.liu.exception.UserException;
import com.liu.mapper.UserMapper;
import com.liu.model.User;
import com.liu.pojo.Result;
import com.liu.repository.UserRepository;
import com.liu.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class userServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public int addUser(User user) {
        int flag = userMapper.insert(user);
        return flag;
    }

    @Override
    public List<User> findAllUser(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return userMapper.selectAllUser();
    }

    @Override
    public void register(JmlUser user) throws Exception {
        Result result = new Result();
        JmlUser jmlUser = userRepository.findByPhoneNumber(user.getPhoneNumber());
        if(null != jmlUser){
            throw new UserException(ResultEmum.PHONE_EXIST);
        }
        JmlUser user1 = userRepository.save(user);
        if(null != user1){
            throw new UserException(ResultEmum.REGISTER_SUCCESS,user1);
        }else{
            throw new UserException(ResultEmum.REGISTER_ERROR);
        }
    }
}
