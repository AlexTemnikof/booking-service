package com.example.gateway.security;

import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class JwtConfig {

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(
            @Value("${security.jwt.secret:dev-secret-please-change}") String secret) {
        return NimbusReactiveJwtDecoder.withSecretKey(JwtSecretKeyProvider.getHmacKey(secret)).build();
    }
}
