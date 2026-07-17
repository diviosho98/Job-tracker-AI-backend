package org.example.jobtrackerai.applicationService;

import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;

import java.util.List;

public interface ApplicationService {
    List<ApplicationResponseDTO> getAllApplication();
    ApplicationResponseDTO createApplication(CreateApplicationDTO application);
    List<ApplicationResponseDTO> getApplicationByUserId(Long id);
    ApplicationResponseDTO updateApplication(Long id, CreateApplicationDTO application);
    void deleteApplication(Long id);
}
