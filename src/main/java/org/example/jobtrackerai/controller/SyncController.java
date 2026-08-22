package org.example.jobtrackerai.controller;

import org.example.jobtrackerai.DTO.SyncResponseDTO;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.exception.RateLimitException;
import org.example.jobtrackerai.exception.ResourceNotFoundException;
import org.example.jobtrackerai.repository.UserRepository;
import org.example.jobtrackerai.service.SyncService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class SyncController {

    private static final int SYNC_COOLDOWN_MINUTES = 5;

    private final SyncService syncService;
    private final UserRepository userRepository;
    private final Map<String, LocalDateTime> lastSyncTimes = new ConcurrentHashMap<>();

    public SyncController(SyncService syncService, UserRepository userRepository) {
        this.syncService = syncService;
        this.userRepository = userRepository;
    }

    @PostMapping("/sync")
    public SyncResponseDTO sync(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");

        LocalDateTime lastSync = lastSyncTimes.get(email);
        if (lastSync != null && lastSync.plusMinutes(SYNC_COOLDOWN_MINUTES).isAfter(LocalDateTime.now())) {
            throw new RateLimitException("Please wait " + SYNC_COOLDOWN_MINUTES +
                    " minutes between syncs");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        SyncResponseDTO result = syncService.sync(user);
        lastSyncTimes.put(email, LocalDateTime.now());
        return result;
    }
}
