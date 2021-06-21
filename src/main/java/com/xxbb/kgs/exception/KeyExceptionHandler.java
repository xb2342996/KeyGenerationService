package com.xxbb.kgs.exception;

import com.xxbb.kgs.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KeyExceptionHandler {

    @ExceptionHandler(RedisKeyException.class)
    public R<Void> keyException(RedisKeyException e) {
        return R.failure(e.getMsg());
    }
}
