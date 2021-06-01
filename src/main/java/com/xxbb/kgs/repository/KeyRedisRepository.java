package com.xxbb.kgs.repository;

import com.xxbb.kgs.core.Key;
import com.xxbb.kgs.exception.Assert;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service("keyRepository")
public class KeyRedisRepository implements KeyRepository{

    private final Logger logger = LoggerFactory.getLogger(KeyRepository.class);
    private final static String UNUSEDKEY = "unusedKey";
    private final static String USEDKEY = "usedKey";
    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Override
    public Mono<Void> saveUnusedKeys(Flux<String> keys) {
        AtomicLong counter = new AtomicLong(0);
        return keys.buffer(100)
                .flatMap(keyList ->
                        reactiveRedisTemplate
                                .opsForSet()
                                .add(UNUSEDKEY, keyList.toArray(new String[0]))
                                .subscribeOn(Schedulers.parallel()))
                .publishOn(Schedulers.parallel())
                .filter(aLong -> aLong > 0)
                .switchIfEmpty(Mono.error(new RedisKeyException("Key saved Error")))
                .buffer(10000)
                .map(longs -> longs.stream().mapToLong(Long::longValue).sum())
                .doOnNext(processedElements -> logger.info("Processed {} elements", counter.addAndGet(processedElements)))
                .then();
    }

    @Override
    public List<String> getUnusedKeys(long count) {
        Assert.isTrue(count >= 0, "Number of Keys cannot be negative, Please retry...");
        return Mono.justOrEmpty(count).flatMapMany(this::popKeys).toStream().collect(Collectors.toList());
    }

    private Flux<String> popKeys(long count) {
        return reactiveRedisTemplate.opsForSet().pop(UNUSEDKEY, count)
                .flatMap(key -> reactiveRedisTemplate.opsForSet()
                        .add(USEDKEY, key)
                        .filter(aLong -> aLong == 1)
                        .map(aLong -> (String) key));
    }
}
