package org.example.jobtrackerai.applicationService.impl;

import org.example.jobtrackerai.Model.Application;
import org.example.jobtrackerai.Model.ApplicationSource;
import org.example.jobtrackerai.Model.ApplicationStatus;
import org.example.jobtrackerai.Model.User;
import org.example.jobtrackerai.applicationService.ApplicationService;
import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;
import org.example.jobtrackerai.exception.ResourceNotFoundException;
import org.example.jobtrackerai.repository.ApplicationRepository;
import org.example.jobtrackerai.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationServiceimpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public ApplicationServiceimpl(ApplicationRepository applicationRepository,
                                  UserRepository userRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ApplicationResponseDTO> getApplications() {
        User currentUser = getCurrentUser();
        List<Application> response = applicationRepository.findByUser(currentUser);
        List<ApplicationResponseDTO> applicationResponseDTOS = new ArrayList<>(response.size());
        for (Application application : response) {
            applicationResponseDTOS.add(ApplicationResponseDTO.convert(application));
        }
        return applicationResponseDTOS;
    }

    @Override
    public ApplicationResponseDTO createApplication(CreateApplicationDTO app) {
        User currentUser = getCurrentUser();
        Application applicationEntity = new Application();
        applicationEntity.setRole(app.role());
        applicationEntity.setUser(currentUser);
        applicationEntity.setCompany(app.company());
        applicationEntity.setSource(ApplicationSource.MANUAL);
        applicationEntity.setStatus(
                app.status() != null ? app.status() : ApplicationStatus.APPLIED
        );
        Application response = applicationRepository.save(applicationEntity);
        return ApplicationResponseDTO.convert(response);
    }

    @Override
    public ApplicationResponseDTO getApplicationById(Long id) {
        User currentUser = getCurrentUser();
        Application app = applicationRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return ApplicationResponseDTO.convert(app);
    }

    @Override
    public ApplicationResponseDTO updateApplication(Long id, CreateApplicationDTO app) {
        User currentUser = getCurrentUser();
        Application existing = applicationRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));

        existing.setCompany(app.company());
        existing.setRole(app.role());
        existing.setStatus(app.status() != null ? app.status() : existing.getStatus());
        existing.setSource(ApplicationSource.MANUAL);

        Application saved = applicationRepository.save(existing);
        return ApplicationResponseDTO.convert(saved);
    }

    @Override
    public void deleteApplication(Long id) {
        User currentUser = getCurrentUser();
        Application existing = applicationRepository.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        applicationRepository.delete(existing);
    }

    private User getCurrentUser() {
        OAuth2User oAuth2User = (OAuth2User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String email = oAuth2User.getAttribute("email");
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
