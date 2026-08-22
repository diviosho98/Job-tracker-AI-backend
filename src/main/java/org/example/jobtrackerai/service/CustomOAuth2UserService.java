package org.example.jobtrackerai.service;

import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        String accessToken = userRequest.getAccessToken().getTokenValue();

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setProfilePicture(picture);
                    return newUser;
                });

        user.setGmailAccessToken(accessToken);
        if (userRequest.getAccessToken().getExpiresAt() != null) {
            user.setTokenExpiresAt(
                    LocalDateTime.ofInstant(userRequest.getAccessToken().getExpiresAt(),
                            java.time.ZoneId.systemDefault())
            );
        }

        userRepository.save(user);

        return oAuth2User;
    }
}
