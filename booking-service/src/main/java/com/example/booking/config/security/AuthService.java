package com.example.booking.config.security;

import com.example.booking.core.user.User;
import com.example.booking.core.user.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class AuthService {
    private static final int MIN_SECRET_KEY_LENGTH = 32;
    private static final int TOKEN_EXPIRATION_SECONDS = 3600;

    private final UserRepository userRepository;
    private final SecretKey secretKey;

    public AuthService(
            UserRepository userRepository,
            @Value("${security.jwt.secret}") String secret
    ) {
        this.userRepository = userRepository;
        this.secretKey = generateSecretKey(secret);
    }

    public User register(String username, String password, boolean isAdmin) {
        User user = createUser(username, password, isAdmin);
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        final User user = authenticateUser(username, password);
        return generateToken(user);
    }

    private User createUser(String username, String password, boolean isAdmin) {
        final String passwordHash = hashPassword(password);
        final String role = determineUserRole(isAdmin);

        return new User()
                .withUsername(username)
                .withPasswordHash(passwordHash)
                .withRole(role);
    }

    private User authenticateUser(String username, String password) {
        final User user = findUserByUsername(username);
        validatePassword(password, user.getPasswordHash());
        return user;
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private void validatePassword(String rawPassword, String passwordHash) {
        if (!BCrypt.checkpw(rawPassword, passwordHash)) {
            throw new IllegalArgumentException("Bad credentials");
        }
    }

    private String generateToken(User user) {
        final Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .addClaims(createTokenClaims(user))
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(TOKEN_EXPIRATION_SECONDS)))
                .signWith(secretKey)
                .compact();
    }

    private Map<String, Object> createTokenClaims(User user) {
        return Map.of(
                "scope", user.getRole(),
                "username", user.getUsername()
        );
    }

    private SecretKey generateSecretKey(String secret) {
        final byte[] keyBytes = ensureKeyLength(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] ensureKeyLength(String secret) {
        final byte[] bytes = secret.getBytes(StandardCharsets.UTF_8);

        if (bytes.length >= MIN_SECRET_KEY_LENGTH) {
            return bytes;
        }

        return padKeyToRequiredLength(bytes);
    }

    private byte[] padKeyToRequiredLength(byte[] originalBytes) {
        final byte[] paddedBytes = new byte[MIN_SECRET_KEY_LENGTH];
        System.arraycopy(originalBytes, 0, paddedBytes, 0, originalBytes.length);
        return paddedBytes;
    }

    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    private String determineUserRole(boolean isAdmin) {
        return isAdmin ? "ADMIN" : "USER";
    }
}