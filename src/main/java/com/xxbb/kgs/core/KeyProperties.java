package com.xxbb.kgs.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "kgs.keys")
@Component
public class KeyProperties {
    private int length;
    private boolean load;

    public boolean isLoad() {
        return load;
    }

    public void setLoad(boolean load) {
        this.load = load;
    }

    public int length() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }
}
