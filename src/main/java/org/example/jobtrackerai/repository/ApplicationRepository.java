package org.example.jobtrackerai.repository;


import org.example.jobtrackerai.Model.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByUserId(Long id);
    Optional<Application> findByIdAndUserId(Long id, Long userId);
}
