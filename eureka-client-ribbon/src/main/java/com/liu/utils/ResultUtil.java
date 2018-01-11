package com.liu.utils;

import com.liu.pojo.Result;

public class ResultUtil {

    public static Result success(Object data){
        Result result = new Result();
        result.setCode(0);
        result.setMessage("成功");
        result.setData(data);
        return  result;
    }

    public static Result hander(Integer code,String message, Object data){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return  result;
    }

    public static Result hander(Integer code,String message){
        return  hander(code,message,null);
    }

    public static Result success(){
        return  success(null);
    }

    public static Result error(Integer code,String message){
        Result result = new Result();
        result.setCode(code);
        result.setMessage(message);
        result.setData(null);
        return  result;
    }
}
