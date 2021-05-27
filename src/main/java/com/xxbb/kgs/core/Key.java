package com.xxbb.kgs.core;

public class Key {
    private final String key;

    public Key(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}
