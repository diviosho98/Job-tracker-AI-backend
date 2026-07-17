package org.example.jobtrackerai.controller;

import jakarta.validation.Valid;
import org.example.jobtrackerai.applicationService.ApplicationService;
import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.example.jobtrackerai.DTO.CreateApplicationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/application-v1")
public class ApplicationController {
    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }
     @GetMapping("/applications")
     public List<ApplicationResponseDTO> getApplications() {
         return applicationService.getAllApplication();
     }

     @GetMapping("/applications/{id}")
     public List<ApplicationResponseDTO> getApplicationById(@PathVariable Long id) {
        return applicationService.getApplicationByUserId(id);
     }

     @PostMapping("/applications")
     @ResponseStatus(HttpStatus.CREATED)
    public ApplicationResponseDTO addApplication(@Valid @RequestBody CreateApplicationDTO createApplicationDTO) {
        return applicationService.createApplication(createApplicationDTO);
    }

    @PutMapping("/applications/{id}")
    public ApplicationResponseDTO updateApplication(@PathVariable Long id, @Valid @RequestBody  CreateApplicationDTO createApplicationDTO) {
        return applicationService.updateApplication(id, createApplicationDTO);
    }

    @DeleteMapping("/applications/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
    }
}
