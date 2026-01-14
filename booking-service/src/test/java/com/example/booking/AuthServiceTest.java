package com.example.booking;

import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import com.example.booking.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private UserRepository userRepository;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authService = new AuthService(userRepository, "dev-secret-please-change");
    }

    @Test
    void registerAndLogin_success() {
        String rawPassword = "password";
        String hashedPassword = org.springframework.security.crypto.bcrypt.BCrypt.hashpw(rawPassword, org.springframework.security.crypto.bcrypt.BCrypt.gensalt());

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("test");
        savedUser.setPasswordHash(hashedPassword);
        savedUser.setRole("USER");

        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(userRepository.findByUsername("test")).thenReturn(Optional.of(savedUser));

        User u = authService.register("test", rawPassword, false);
        assertNotNull(u.getId());
        assertEquals("test", u.getUsername());

        String token = authService.login("test", rawPassword);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT состоит из 3 частей
    }

}
