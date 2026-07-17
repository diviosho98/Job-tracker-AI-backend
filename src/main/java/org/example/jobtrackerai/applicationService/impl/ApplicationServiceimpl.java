package org.example.jobtrackerai.applicationService.impl;

import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationStatus;
import org.example.jobtrackerai.applicationService.ApplicationService;
import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;
import org.example.jobtrackerai.repository.ApplicationRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationServiceimpl implements ApplicationService {
    private final ApplicationRepository applicationRepository;
    public ApplicationServiceimpl(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    @Override
    public List<ApplicationResponseDTO> getAllApplication() {
        List<Application> response = applicationRepository.findAll();
        List<ApplicationResponseDTO> applicationResponseDTOS = new ArrayList<>(response.size());
        for (Application application : response) {
            applicationResponseDTOS.add(ApplicationResponseDTO.convert(application));
        }
        return applicationResponseDTOS;
    }

    @Override
    public ApplicationResponseDTO createApplication(CreateApplicationDTO app) {
        Application applicationEntity = new Application();
        applicationEntity.setRole(app.role());
        applicationEntity.setUserId(1L);
        applicationEntity.setCompany(app.company());
        applicationEntity.setStatus(
                app.status() != null ? app.status() : ApplicationStatus.APPLIED
        );
        Application response = applicationRepository.save(applicationEntity);
        return ApplicationResponseDTO.convert(response);
    }

    @Override
    public List<ApplicationResponseDTO> getApplicationByUserId(Long id) {
        List<Application> response  = applicationRepository.findByUserId(id);
        List<ApplicationResponseDTO> applicationResponseDTOS = new ArrayList<>(response.size());
        for (Application application : response) {
            applicationResponseDTOS.add(ApplicationResponseDTO.convert(application));
        }
        return applicationResponseDTOS;
    }

    @Override
    public ApplicationResponseDTO updateApplication(Long id, CreateApplicationDTO app) {
        Long currentUserId = 1L;   // later: from authenticated session

        Application existing = applicationRepository.findByIdAndUserId(id, currentUserId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + id));

        existing.setCompany(app.company());
        existing.setRole(app.role());
        existing.setStatus(app.status() != null ? app.status() : existing.getStatus());

        Application saved = applicationRepository.save(existing);

        return ApplicationResponseDTO.convert(saved);
    }

    @Override
    public void deleteApplication(Long id) {
        if (applicationRepository.existsById(id)) {
            applicationRepository.deleteById(id);
        }
    }
}
