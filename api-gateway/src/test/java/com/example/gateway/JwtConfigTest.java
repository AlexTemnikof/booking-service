package com.example.gateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JwtConfigTest {

    @Autowired
    private ReactiveJwtDecoder reactiveJwtDecoder;

    @Test
    void reactiveJwtDecoderBeanShouldBeCreated() {
        assertThat(reactiveJwtDecoder).isNotNull();
    }
}
