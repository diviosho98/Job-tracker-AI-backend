package org.example.jobtrackerai.DTO;

import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponseDTO (
        Long id,
        Long userId,
        String company,
        String role,
        ApplicationStatus status,
        LocalDateTime appliedDate,
        LocalDateTime lastUpdatedAt
){
    public static ApplicationResponseDTO convert(Application application) {
        return new ApplicationResponseDTO(
                application.getId(),
                application.getUserId(),
                application.getCompany(),
                application.getRole(),
                application.getStatus(),
                application.getAppliedDate(),
                application.getLastUpdatedAt()
        );
    }
}
