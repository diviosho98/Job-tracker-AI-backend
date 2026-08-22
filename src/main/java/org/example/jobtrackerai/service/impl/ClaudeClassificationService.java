package org.example.jobtrackerai.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.jobtrackerai.DTO.ClassificationResult;
import org.example.jobtrackerai.Model.ApplicationStatus;
import org.example.jobtrackerai.service.ClassificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClaudeClassificationService implements ClassificationService {

    private static final Logger log = LoggerFactory.getLogger(ClaudeClassificationService.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final boolean enabled;

    public ClaudeClassificationService(@Value("${app.anthropic.api-key:}") String apiKey,
                                        ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.enabled = apiKey != null && !apiKey.isBlank();
        this.restClient = RestClient.builder()
                .baseUrl("https://api.anthropic.com")
                .defaultHeader("x-api-key", apiKey != null ? apiKey : "")
                .defaultHeader("anthropic-version", "2023-06-01")
                .build();
    }

    @Override
    public Optional<ClassificationResult> classify(String subject, String sender, String body) {
        if (!enabled) {
            log.warn("Classification skipped: ANTHROPIC_API_KEY not configured");
            return Optional.empty();
        }
        try {
            String prompt = buildPrompt(subject, sender, body);

            Map<String, Object> request = Map.of(
                    "model", "claude-sonnet-4-20250514",
                    "max_tokens", 256,
                    "messages", List.of(
                            Map.of("role", "user", "content", prompt)
                    )
            );

            String responseBody = restClient.post()
                    .uri("/v1/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            return parseResponse(responseBody);
        } catch (Exception e) {
            log.error("Classification failed for email: {}", subject, e);
            return Optional.empty();
        }
    }

    private String buildPrompt(String subject, String sender, String body) {
        String truncatedBody = body.length() > 2000 ? body.substring(0, 2000) : body;

        return """
                Analyze this email and determine if it's related to a job application.
                If it is, extract the following information as JSON:

                {
                  "is_job_related": true/false,
                  "company": "company name",
                  "role": "job title/role",
                  "status": "APPLIED|ACKNOWLEDGED|INTERVIEWING|REJECTED|SELECTED",
                  "confidence": 0.0-1.0
                }

                Status guide:
                - APPLIED: confirmation that application was submitted
                - ACKNOWLEDGED: company acknowledged receipt of application
                - INTERVIEWING: invitation to interview or next round
                - REJECTED: rejection or "we've decided to move forward with other candidates"
                - SELECTED: offer letter or congratulations on being selected

                If the email is not job-related, return: {"is_job_related": false}

                Return ONLY the JSON, no other text.

                Subject: %s
                From: %s
                Body: %s
                """.formatted(subject, sender, truncatedBody);
    }

    private Optional<ClassificationResult> parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            String text = root.path("content").get(0).path("text").asText();

            String json = text.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json?\\s*", "").replaceAll("```\\s*$", "").trim();
            }

            JsonNode result = objectMapper.readTree(json);

            if (!result.path("is_job_related").asBoolean(false)) {
                return Optional.empty();
            }

            ApplicationStatus status = ApplicationStatus.valueOf(
                    result.path("status").asText("APPLIED"));

            return Optional.of(new ClassificationResult(
                    result.path("company").asText("Unknown"),
                    result.path("role").asText("Unknown"),
                    status,
                    result.path("confidence").asDouble(0.5)
            ));
        } catch (Exception e) {
            log.error("Failed to parse classification response", e);
            return Optional.empty();
        }
    }
}
