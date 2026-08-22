package org.example.jobtrackerai.repository;

import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationSource;
import org.example.jobtrackerai.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUser(User user);
    Optional<Application> findByIdAndUser(Long id, User user);
    Optional<Application> findByUserAndCompanyAndRole(User user, String company, String role);
}
