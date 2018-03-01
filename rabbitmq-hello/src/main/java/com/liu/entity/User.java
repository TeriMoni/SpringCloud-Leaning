package com.liu.entity;

import java.io.Serializable;

/**
 * 测试传输实体咧
 * 用于rabbitmq传输需要实现序列化
 * @author: bin.liu
 * @create: 2018-03-01 15:22
 **/
public class User implements Serializable {

    private String userName;

    private String passWord;

    public User(String userName, String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", passWord='" + passWord + '\'' +
                '}';
    }
}
