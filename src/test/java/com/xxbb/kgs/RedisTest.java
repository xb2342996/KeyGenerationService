package com.xxbb.kgs;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    public void testRedisConnection() {
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set("x", "y");
        String result = ops.get("x");
        System.out.println(result);
        Assert.assertEquals(result, "y");
    }
}
