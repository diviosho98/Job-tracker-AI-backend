package org.example.jobtrackerai.ApplicationService;

import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponseDTO> getAllApplication();
    ApplicationResponseDTO createApplication(CreateApplicationDTO application);
    ApplicationResponseDTO getApplicationById(Long id);
    ApplicationResponseDTO updateApplication(Long id, CreateApplicationDTO application);
    void deleteApplication(Long id);
}
