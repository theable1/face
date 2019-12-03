package com.ffcs.face;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@TestConfiguration
public class TestConfig {
    @Bean
    public RestTemplate restTemplate1(){
        return new RestTemplate();
    }
}
