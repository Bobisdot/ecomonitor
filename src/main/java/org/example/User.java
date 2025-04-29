package org.example;

public record User(
        int id,
        String username,
        String password,
        String email,
        String role
) {}