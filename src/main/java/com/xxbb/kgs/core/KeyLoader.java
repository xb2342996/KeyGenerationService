package com.xxbb.kgs.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxbb.kgs.repository.KeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static com.xxbb.kgs.common.Constants.UNUSEDKEY;

@Component
public class KeyLoader {
    private static final Logger logger = LoggerFactory.getLogger(KeyLoader.class);
    private static final String LOADED_LOCK = "kgs:isLoad";
    private static final Boolean FLAG = true;
    private final ReactiveRedisConnectionFactory factory;
    private final KeyGenerator generator;
    @Autowired
    private KeyRepository keyRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public KeyLoader(KeyGenerator generator, ReactiveRedisConnectionFactory factory) {
        this.generator = generator;
        this.factory = factory;
    }

    @PostConstruct
    public void loadKeys() {
        if (getLock()) {
            final Long[] times = new Long[2];
            factory.getReactiveConnection()
                    .keyCommands()
                    .del(ByteBuffer.wrap(UNUSEDKEY.getBytes()))
                    .subscribeOn(Schedulers.elastic())
                    .flatMapMany(s -> generator.generateKeys())
                    .publishOn(Schedulers.parallel())
                    .transform(keyRepository::saveUnusedKeys)
                    .subscribeOn(Schedulers.parallel())
                    .doOnSubscribe(subscription -> times[0] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .doOnComplete(() -> times[1] = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC))
                    .subscribe(
                            it -> logger.trace("Batch Processed"),
                            ex -> logger.error(ex.getLocalizedMessage(), ex),
                            () -> logger.info(String.format("key reset completed in: %d seconds", (times[1] - times[0]))));
        }
    }

    private boolean getLock() {
        Object lock = redisTemplate.opsForValue().get(LOADED_LOCK);
        if (lock == null) {
            redisTemplate.opsForValue().set(LOADED_LOCK, FLAG);
            return true;
        }
        return false;
    }
}
