package org.example.jobtrackerai.ApplicationService.impl;

import org.example.jobtrackerai.ApplicationService.ApplicationService;
import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationServiceimpl implements ApplicationService {

    @Override
    public List<ApplicationResponseDTO> getAllApplication() {
        return List.of();
    }

    @Override
    public ApplicationResponseDTO createApplication(CreateApplicationDTO application) {
        return null;
    }

    @Override
    public ApplicationResponseDTO getApplicationById(Long id) {
        return null;
    }

    @Override
    public ApplicationResponseDTO updateApplication(Long id, CreateApplicationDTO application) {
        return null;
    }

    @Override
    public void deleteApplication(Long id) {

    }
}
