package com.liu.exception;

import com.liu.pojo.Result;
import com.liu.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ExceptionHander {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHander.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result Hander(Exception e){
        if(e instanceof UserException){
            return ResultUtil.hander(((UserException) e).getCode(),e.getMessage(),((UserException) e).getData());
        }else if(e instanceof ValidateException){
            return ResultUtil.hander(((ValidateException) e).getCode(),e.getMessage());
        }else{
            logger.error("系统异常-->{}",e);
            return ResultUtil.hander(-1,"未知错误");
        }
    }
}
