package com.liu.emum;

public enum ResultEmum {
    UNKNOWN_ERROR(-1,"未知错误"),
    REGISTER_SUCCESS(1000,"注册成功"),
    REGISTER_ERROR(1004,"注册失败"),
    PHONE_EXIST(1002,"手机号已被注册"),
    SEND_ERROR(1004,"发送失败"),
    SEND_SUCCESS(1000,"发送成功"),
    UPLOAD_AVATAR_ERROR(1001,"上传头像失败"),
    PHONE_REGEX_ERROR(1001,"手机号格式不正确"),;
    private Integer code;
    private String message;

    ResultEmum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

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
}
