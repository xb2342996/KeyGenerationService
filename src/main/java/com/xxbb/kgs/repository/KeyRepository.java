package com.xxbb.kgs.repository;

import com.xxbb.kgs.core.Key;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface KeyRepository {
    Mono<Void> saveUnusedKeys(Flux<Key> keys);
    Flux<Key> getUnusedKeys(long count);
}
