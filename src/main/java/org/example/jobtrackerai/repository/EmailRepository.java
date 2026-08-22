package org.example.jobtrackerai.repository;

import org.example.jobtrackerai.Model.Email;
import org.example.jobtrackerai.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailRepository extends JpaRepository<Email, Long> {
    boolean existsByGmailMessageId(String gmailMessageId);
    List<Email> findByUserAndProcessedFalse(User user);
}
