package com.xxbb.kgs.controller;

import com.xxbb.kgs.core.Key;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class KeyController {

    @GetMapping("/keys")
    public List<Key> getKeys() {
        return new ArrayList<>();
    }

}
