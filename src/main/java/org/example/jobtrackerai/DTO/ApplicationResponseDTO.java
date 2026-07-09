package org.example.jobtrackerai.DTO;

import org.example.jobtrackerai.Model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponseDTO (
        Long id,
        String company,
        String role,
        ApplicationStatus status,
        LocalDateTime appliedDate,
        LocalDateTime lastUpdatedAt
){}
