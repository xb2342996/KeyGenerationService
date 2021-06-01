package com.xxbb.kgs.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeyGenerator {
    private static final Logger logger = LoggerFactory.getLogger(KeyGenerator.class);
    private final Base64Dictionary base64Dictionary;
    private final KeyProperties keyProperties;

    public KeyGenerator(Base64Dictionary base64Dictionary, KeyProperties properties) {
        this.base64Dictionary = base64Dictionary;
        this.keyProperties = properties;
    }

    public KeyProperties getKeyProperties() {
        return keyProperties;
    }

    public Flux<String> generateKeys() {
        return Flux.merge(
                Flux.range(0, keyProperties.length())
                        .reduce(Flux.just(""), (keyFlux, i) -> keyFlux.transform(this::addCharacter)))
                .doOnSubscribe(subscription -> logger.info(
                        "Starting the generation of {} keys",
                        BigDecimal.valueOf(Math.pow(64, keyProperties.length())).toBigInteger()
                ))
                .subscribeOn(Schedulers.parallel());
    }

    private Flux<String> addCharacter(Flux<String> in) {
        List<Character> dict = shuffleDictionary();
        return in.flatMap(s ->
                Flux.range(0, dict.size())
                        .map(i -> s + dict.get(i))
                        .subscribeOn(Schedulers.parallel()));
    }

    private List<Character> shuffleDictionary() {
        List<Character> dict = Arrays.asList(this.base64Dictionary.getCharacters());
        Collections.shuffle(dict);
        return dict;
    }

}
