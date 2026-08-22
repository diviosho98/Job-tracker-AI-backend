package org.example.jobtrackerai.controller;

import org.example.jobtrackerai.DTO.UserResponseDTO;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.exception.ResourceNotFoundException;
import org.example.jobtrackerai.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    public UserResponseDTO getCurrentUser(@AuthenticationPrincipal OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return UserResponseDTO.convert(user);
    }

    @GetMapping("/status")
    public Map<String, Boolean> authStatus(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return Map.of("authenticated", oAuth2User != null);
    }
}
