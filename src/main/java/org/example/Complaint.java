package org.example;



import java.time.LocalDateTime;

public record Complaint(
        int id,
        String title,
        String description,
        String location,
        String complaintType,
        String status,
        LocalDateTime createdAt,
        int userId
) {}