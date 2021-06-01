package com.xxbb.kgs.exception;

public abstract class Assert {

    public static void isTrue(Boolean expression, String message) {
        if (!expression) {
            throw new RedisKeyException(message);
        }
    }
}
