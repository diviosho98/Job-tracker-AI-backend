package org.example.jobtrackerai.DTO;

import jakarta.validation.constraints.NotBlank;
import org.example.jobtrackerai.Model.ApplicationStatus;

public record CreateApplicationDTO (
        @NotBlank(message = "Company name is required") String company,
        @NotBlank(message = "Role is required") String role,
        ApplicationStatus status
){}