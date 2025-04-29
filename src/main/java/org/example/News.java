package org.example;

import java.time.LocalDateTime;

public record News(
        int id,
        String title,
        String content,
        LocalDateTime createdAt
) {}
