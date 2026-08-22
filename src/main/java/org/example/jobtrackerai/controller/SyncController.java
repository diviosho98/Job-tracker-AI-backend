package org.example.jobtrackerai.controller;

import org.example.jobtrackerai.DTO.SyncResponseDTO;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.exception.ResourceNotFoundException;
import org.example.jobtrackerai.repository.UserRepository;
import org.example.jobtrackerai.service.SyncService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SyncController {

    private final SyncService syncService;
    private final UserRepository userRepository;

    public SyncController(SyncService syncService, UserRepository userRepository) {
        this.syncService = syncService;
        this.userRepository = userRepository;
    }

    @PostMapping("/sync")
    public SyncResponseDTO sync(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return syncService.sync(user);
    }
}
