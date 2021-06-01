package com.xxbb.kgs.exception;

public class RedisKeyException extends RuntimeException {

    private String msg;

    public RedisKeyException() {
    }

    public RedisKeyException(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
