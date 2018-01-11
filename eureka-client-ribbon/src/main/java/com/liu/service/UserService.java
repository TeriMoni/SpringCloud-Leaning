package com.liu.service;

import com.liu.entity.JmlUser;
import com.liu.model.User;

import java.util.List;

public interface UserService {

    public int addUser(User user);

    public List<User> findAllUser(int pageNum, int pageSize);

    public void register(JmlUser user) throws Exception;
}
