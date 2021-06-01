package com.xxbb.kgs.config;

import com.xxbb.kgs.core.Base64Dictionary;
import com.xxbb.kgs.core.KeyGenerator;
import com.xxbb.kgs.core.KeyProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContainerConfiguration {

    @Autowired
    private KeyProperties properties;

    @Bean
    public Base64Dictionary base64Dictionary() {
        return new Base64Dictionary();
    }

    @Bean
    public KeyGenerator keyGenerator(Base64Dictionary base64Dictionary) {
        return new KeyGenerator(base64Dictionary, properties);
    }
}
