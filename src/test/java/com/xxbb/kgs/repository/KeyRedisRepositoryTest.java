package com.xxbb.kgs.repository;

import com.xxbb.kgs.exception.RedisKeyException;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class KeyRedisRepositoryTest {

    @Autowired
    private KeyRepository keyRepository;

    @Test
    void getUnusedKeys() {
        List<String> keys = keyRepository.getUnusedKeys(10);
        for (String key : keys) {
            System.out.println(key);
        }
        assertEquals(keys.size(), 10);
    }

    @Test
    void getUnusedKeysWithZero() {
        List<String> keys = keyRepository.getUnusedKeys(0);
        assertEquals(keys.size(), 0);
    }

    @Test
    public void getUnusedKeysWithNegative() {
        assertThrows(RedisKeyException.class, () -> keyRepository.getUnusedKeys(-1), "Number of Keys cannot be negative, Please retry...");
    }
}