package com.liu.exception;

import com.liu.emum.ResultEmum;

public class UserException extends RuntimeException {

    private Integer code ;

    private Object data;

    public UserException(ResultEmum resultEmum, Object data) {
        super(resultEmum.getMessage());
        this.code = resultEmum.getCode();
        this.data = data;
    }

    public UserException(ResultEmum resultEmum) {
        super(resultEmum.getMessage());
        this.code = resultEmum.getCode();
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
