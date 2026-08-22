package org.example.jobtrackerai.DTO;

import org.example.jobtrackerai.Model.ApplicationStatus;

public record ClassificationResult(
        String company,
        String role,
        ApplicationStatus status,
        double confidence
) {}
