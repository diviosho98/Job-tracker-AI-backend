package org.example.jobtrackerai.service;

import org.example.jobtrackerai.DTO.ClassificationResult;

import java.util.Optional;

public interface ClassificationService {
    Optional<ClassificationResult> classify(String subject, String sender, String body);
}
