package com.example.gateway;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.reactive.server.WebTestClient;
import com.example.gateway.security.JwtSecretKeyProvider;

import java.util.Date;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SecurityTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebTestClient webClient;

    private static final String SECRET = "dev-secret-please-change";

    private String generateJwt() {
        byte[] keyBytes = JwtSecretKeyProvider.getHmacKey(SECRET).getEncoded();

        return Jwts.builder()
                .setSubject("test-user")
                .setExpiration(new Date(System.currentTimeMillis() + 3600_000))
                .signWith(Keys.hmacShaKeyFor(keyBytes))
                .compact();
    }



    @Test
    void shouldAllowAccessToPublicEndpoints() {
        webClient.get()
                .uri("/auth/login")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Public endpoint");
    }

    @Test
    void shouldDenyAccessToProtectedEndpointsWithoutToken() {
        webClient.get()
                .uri("/protected/resource")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void shouldAllowAccessToProtectedEndpointsWithValidJwt() {
        String token = generateJwt();

        webClient.get()
                .uri("/protected/resource")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Protected endpoint");
    }
}
