package org.example.jobtrackerai.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import org.example.jobtrackerai.Model.Email;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {

    private static final Logger log = LoggerFactory.getLogger(GmailService.class);
    private static final String JOB_EMAIL_QUERY =
            "subject:(application OR applied OR interview OR offer OR rejected OR " +
            "\"thank you for applying\" OR \"we received your application\" OR " +
            "\"next steps\" OR \"hiring\" OR \"position\")";

    private final EmailRepository emailRepository;

    public GmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public List<Email> fetchJobEmails(User user) {
        if (user.getGmailAccessToken() == null) {
            log.warn("No Gmail access token for user {}", user.getEmail());
            return Collections.emptyList();
        }

        try {
            Gmail gmail = buildGmailClient(user);
            ListMessagesResponse response = gmail.users().messages()
                    .list("me")
                    .setQ(JOB_EMAIL_QUERY)
                    .setMaxResults(50L)
                    .execute();

            if (response.getMessages() == null) {
                return Collections.emptyList();
            }

            List<Email> newEmails = new ArrayList<>();
            for (Message msgRef : response.getMessages()) {
                if (emailRepository.existsByGmailMessageId(msgRef.getId())) {
                    continue;
                }

                Message fullMessage = gmail.users().messages()
                        .get("me", msgRef.getId())
                        .setFormat("full")
                        .execute();

                Email email = convertToEmail(fullMessage, user);
                if (email != null) {
                    newEmails.add(emailRepository.save(email));
                }
            }

            return newEmails;
        } catch (Exception e) {
            log.error("Failed to fetch Gmail messages for user {}", user.getEmail(), e);
            return Collections.emptyList();
        }
    }

    private Gmail buildGmailClient(User user) throws Exception {
        AccessToken accessToken = new AccessToken(user.getGmailAccessToken(), null);
        GoogleCredentials credentials = GoogleCredentials.create(accessToken);

        return new Gmail.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                GsonFactory.getDefaultInstance(),
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Job-tracker-AI")
                .build();
    }

    private Email convertToEmail(Message message, User user) {
        try {
            Email email = new Email();
            email.setUser(user);
            email.setGmailMessageId(message.getId());
            email.setSubject(getHeader(message, "Subject"));
            email.setSender(getHeader(message, "From"));
            email.setBody(extractBody(message));

            if (message.getInternalDate() != null) {
                email.setReceivedAt(LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(message.getInternalDate()),
                        ZoneId.systemDefault()));
            }

            return email;
        } catch (Exception e) {
            log.error("Failed to convert Gmail message {}", message.getId(), e);
            return null;
        }
    }

    private String getHeader(Message message, String name) {
        if (message.getPayload() == null || message.getPayload().getHeaders() == null) {
            return "";
        }
        return message.getPayload().getHeaders().stream()
                .filter(h -> name.equalsIgnoreCase(h.getName()))
                .map(MessagePartHeader::getValue)
                .findFirst()
                .orElse("");
    }

    private String extractBody(Message message) {
        if (message.getPayload() == null) {
            return "";
        }

        if (message.getPayload().getBody() != null
                && message.getPayload().getBody().getData() != null) {
            return new String(Base64.getUrlDecoder().decode(
                    message.getPayload().getBody().getData()));
        }

        if (message.getPayload().getParts() != null) {
            for (var part : message.getPayload().getParts()) {
                if ("text/plain".equals(part.getMimeType())
                        && part.getBody() != null
                        && part.getBody().getData() != null) {
                    return new String(Base64.getUrlDecoder().decode(
                            part.getBody().getData()));
                }
            }
        }

        return message.getSnippet() != null ? message.getSnippet() : "";
    }
}
