package com.xxbb.kgs.repository;

import com.xxbb.kgs.core.Key;
import com.xxbb.kgs.exception.RedisKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.atomic.AtomicLong;

@Service("keyRepository")
public class KeyRedisRepository implements KeyRepository{

    private final Logger logger = LoggerFactory.getLogger(KeyRepository.class);
    private final static String UNUSEDKEY = "unusedKey";
    private final static String USEDKEY = "usedKey";
    @Autowired
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    @Override
    public Mono<Void> saveUnusedKeys(Flux<Key> keys) {
        AtomicLong counter = new AtomicLong(0);
        return keys.buffer(100)
                .flatMap(keyList ->
                        reactiveRedisTemplate
                                .opsForSet()
                                .add(UNUSEDKEY, keyList.toArray(new Key[0]))
                                .subscribeOn(Schedulers.parallel()))
                .publishOn(Schedulers.parallel())
                .filter(aLong -> aLong > 0)
                .switchIfEmpty(Mono.error(new RedisKeyException()))
                .buffer(10000)
                .map(longs -> longs.stream().mapToLong(Long::longValue).sum())
                .doOnNext(processedElements -> logger.info("Processed {} elements", counter.addAndGet(processedElements)))
                .then();
    }

    @Override
    public Flux<Key> getUnusedKeys(long count) {
        return reactiveRedisTemplate.opsForSet().pop(UNUSEDKEY, count)
                .flatMap(key -> reactiveRedisTemplate.opsForSet()
                        .add(USEDKEY, key)
                        .filter(aLong -> aLong == 1)
                        .map(aLong -> (Key) key));
    }
}
