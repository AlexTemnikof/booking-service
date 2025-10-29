package com.example.booking.core.user;

import jakarta.persistence.*;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String passwordHash;
    private String role; // USER или ADMIN

    public Long getId() {
        return id;
    }

    public User withId(final Long id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public User withUsername(final String username) {
        this.username = username;
        return this;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public User withPasswordHash(final String passwordHash) {
        this.passwordHash = passwordHash;
        return this;
    }

    public String getRole() {
        return role;
    }

    public User withRole(final String role) {
        this.role = role;
        return this;
    }
}


