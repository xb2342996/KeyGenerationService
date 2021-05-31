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
    private final int length;

    public KeyGenerator(Base64Dictionary base64Dictionary, int length) {
        this.base64Dictionary = base64Dictionary;
        this.length = length;
    }

    public Flux<Key> generateKeys() {
        return Flux.merge(
                Flux.range(0, length)
                        .reduce(Flux.just(""), (keyFlux, i) -> keyFlux.transform(this::addCharacter)))
                .map(Key::new)
                .doOnSubscribe(subscription -> logger.info(
                        "Starting the generation of {} keys",
                        BigDecimal.valueOf(Math.pow(64, length)).toBigInteger()
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

    public static void main(String[] args) {
        Base64Dictionary b64d = new Base64Dictionary();
        KeyGenerator kg = new KeyGenerator(b64d, 6);
    }
}
