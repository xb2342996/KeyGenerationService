package com.xxbb.kgs.core;

import com.xxbb.kgs.exception.RedisKeyException;
import com.xxbb.kgs.repository.KeyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class KeyLoader {
    private static final Logger logger = LoggerFactory.getLogger(KeyLoader.class);
    private final ReactiveRedisConnectionFactory factory;
    private final KeyGenerator generator;
    @Autowired
    private KeyRepository keyRepository;

    public KeyLoader(KeyGenerator generator, ReactiveRedisConnectionFactory factory) {
        this.generator = generator;
        this.factory = factory;
    }

    @PostConstruct
    public void loadKeys() {
        if (generator.getKeyProperties().isLoad()) {
            final Long[] times = new Long[2];
            factory.getReactiveConnection()
                    .serverCommands()
                    .flushAll()
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

}
