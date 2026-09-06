package com.example.orbitleans.observation.controller;

import com.example.orbitleans.observation.dto.ObservationResponse;
import com.example.orbitleans.observation.service.ObservationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/observations")
public class ObservationController {
    private final ObservationService observationService;

    public ObservationController(ObservationService observationService) {
        this.observationService = observationService;
    }

    @GetMapping
    public ObservationResponse observe(@RequestParam String address, @RequestParam long catalogNumber) {
        return observationService.observe(address, catalogNumber);
    }
}
