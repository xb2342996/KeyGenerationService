package com.xxbb.kgs.repository;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public interface KeyRepository {
    Mono<Void> saveUnusedKeys(Flux<String> keys);
    List<String> getUnusedKeys(long count);
}
