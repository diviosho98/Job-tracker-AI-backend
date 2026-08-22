package org.example.jobtrackerai.DTO;

import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationSource;
import org.example.jobtrackerai.Model.ApplicationStatus;

import java.time.LocalDateTime;

public record ApplicationResponseDTO(
        Long id,
        String company,
        String role,
        ApplicationStatus status,
        ApplicationSource source,
        Double confidence,
        LocalDateTime appliedDate,
        LocalDateTime lastUpdatedAt
) {
    public static ApplicationResponseDTO convert(Application application) {
        return new ApplicationResponseDTO(
                application.getId(),
                application.getCompany(),
                application.getRole(),
                application.getStatus(),
                application.getSource(),
                application.getConfidence(),
                application.getAppliedDate(),
                application.getLastUpdatedAt()
        );
    }
}
