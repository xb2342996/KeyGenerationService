package com.xxbb.kgs.common;

public class R<T> {
    private int code;
    private T data;

    public R(int code, T data) {
        this.code = code;
        this.data = data;
    }

    public static <T> R<T> success(T data) {
        return new R<>(0, data);
    }

    public static  R<String> failure(String message) {
        return new R<>(9999, message);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "R{" +
                "code=" + code +
                ", data=" + data +
                '}';
    }
}
