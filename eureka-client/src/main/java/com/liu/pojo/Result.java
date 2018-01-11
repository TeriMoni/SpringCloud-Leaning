package com.liu.pojo;

/**
 * 返回结果对象
 * @param <T>
 */
public class Result<T> {

    /** 状态码 **/
    private Integer code;

    /** 信息 **/
    private String message;

    /** 返回对象 **/
    private T data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Result{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
