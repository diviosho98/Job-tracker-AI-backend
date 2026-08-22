package org.example.jobtrackerai.configuration;

import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(OAuth2AuthorizedClientService authorizedClientService,
                                     UserRepository userRepository,
                                     @Value("${app.frontend-url}") String frontendUrl) {
        this.authorizedClientService = authorizedClientService;
        this.userRepository = userRepository;
        setDefaultTargetUrl(frontendUrl);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken oauthToken) {
            OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                    oauthToken.getAuthorizedClientRegistrationId(),
                    oauthToken.getName()
            );

            if (client != null && client.getRefreshToken() != null) {
                String email = oauthToken.getPrincipal().getAttribute("email");
                userRepository.findByEmail(email).ifPresent(user -> {
                    user.setGmailRefreshToken(client.getRefreshToken().getTokenValue());
                    userRepository.save(user);
                });
            }
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
