package com.liu.exception;

import com.liu.pojo.Result;

public class ValidateException extends RuntimeException {

    private Integer code ;

    private Object data ;

    public ValidateException(Result result) {
        super(result.getMessage());
        this.code = result.getCode();
        this.data = result.getData();
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
