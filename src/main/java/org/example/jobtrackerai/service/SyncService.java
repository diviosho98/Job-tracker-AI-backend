package org.example.jobtrackerai.service;

import org.example.jobtrackerai.DTO.ClassificationResult;
import org.example.jobtrackerai.DTO.SyncResponseDTO;
import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationSource;
import org.example.jobtrackerai.Model.Email;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.repository.ApplicationRepository;
import org.example.jobtrackerai.repository.EmailRepository;
import org.example.jobtrackerai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final GmailService gmailService;
    private final ClassificationService classificationService;
    private final ApplicationRepository applicationRepository;
    private final EmailRepository emailRepository;
    private final UserRepository userRepository;

    public SyncService(GmailService gmailService,
                       ClassificationService classificationService,
                       ApplicationRepository applicationRepository,
                       EmailRepository emailRepository,
                       UserRepository userRepository) {
        this.gmailService = gmailService;
        this.classificationService = classificationService;
        this.applicationRepository = applicationRepository;
        this.emailRepository = emailRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SyncResponseDTO sync(User user) {
        List<Email> newEmails = gmailService.fetchJobEmails(user);
        int created = 0;
        int updated = 0;

        List<Email> unprocessed = emailRepository.findByUserAndProcessedFalse(user);
        unprocessed.addAll(newEmails.stream()
                .filter(e -> !e.isProcessed())
                .toList());

        for (Email email : unprocessed) {
            Optional<ClassificationResult> result = classificationService.classify(
                    email.getSubject(), email.getSender(), email.getBody());

            if (result.isPresent()) {
                ClassificationResult classification = result.get();

                Optional<Application> existing = applicationRepository
                        .findByUserAndCompanyAndRole(user, classification.company(), classification.role());

                if (existing.isPresent()) {
                    Application app = existing.get();
                    if (app.getSource() == ApplicationSource.AI) {
                        app.setStatus(classification.status());
                        app.setConfidence(classification.confidence());
                        applicationRepository.save(app);
                        updated++;
                    }
                } else {
                    Application app = new Application();
                    app.setUser(user);
                    app.setCompany(classification.company());
                    app.setRole(classification.role());
                    app.setStatus(classification.status());
                    app.setSource(ApplicationSource.AI);
                    app.setConfidence(classification.confidence());
                    applicationRepository.save(app);
                    created++;
                }
            }

            email.setProcessed(true);
            emailRepository.save(email);
        }

        user.setLastSyncedAt(LocalDateTime.now());
        userRepository.save(user);

        log.info("Sync complete for {}: {} emails fetched, {} created, {} updated",
                user.getEmail(), newEmails.size(), created, updated);

        return new SyncResponseDTO(newEmails.size(), created, updated, user.getLastSyncedAt());
    }
}
