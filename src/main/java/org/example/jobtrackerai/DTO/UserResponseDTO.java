package org.example.jobtrackerai.DTO;

import org.example.jobtrackerai.Model.User;

public record UserResponseDTO(
        Long id,
        String email,
        String name,
        String profilePicture
) {
    public static UserResponseDTO convert(User user) {
        return new UserResponseDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getProfilePicture()
        );
    }
}
