package org.example.jobtrackerai.controller;

import org.example.jobtrackerai.ApplicationService.ApplicationService;
import org.example.jobtrackerai.DTO.ApplicationResponseDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
