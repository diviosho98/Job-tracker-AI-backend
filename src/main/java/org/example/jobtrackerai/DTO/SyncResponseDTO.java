package org.example.jobtrackerai.DTO;

import java.time.LocalDateTime;

public record SyncResponseDTO(
        int emailsFetched,
        int applicationsCreated,
        int applicationsUpdated,
        LocalDateTime syncedAt
) {}
