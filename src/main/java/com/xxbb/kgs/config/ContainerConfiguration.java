package com.xxbb.kgs.config;

import com.xxbb.kgs.core.Base64Dictionary;
import com.xxbb.kgs.core.KeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerConfiguration {

    @Value("${kgs.keys.length}")
    private int length;

    @Bean
    public Base64Dictionary base64Dictionary() {
        return new Base64Dictionary();
    }

    @Bean
    public KeyGenerator keyGenerator(Base64Dictionary base64Dictionary) {
        return new KeyGenerator(base64Dictionary, length);
    }
}
