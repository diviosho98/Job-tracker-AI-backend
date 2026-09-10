package org.example.jobtrackerai.service;

import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.repository.UserRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String email = oidcUser.getAttribute("email");
        String name = oidcUser.getAttribute("name");
        String picture = oidcUser.getAttribute("picture");

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

        return oidcUser;
    }
}
