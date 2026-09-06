package com.example.orbitleans.satellite.controller;

import com.example.orbitleans.satellite.domain.Satellite;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import com.example.orbitleans.satellite.service.SatelliteService;
import com.example.orbitleans.satellite.dto.SatellitePositionResponse;
import com.example.orbitleans.orbit.domain.ObserverLocation;
import com.example.orbitleans.orbit.domain.SatellitePass;
import com.example.orbitleans.orbit.domain.SatellitePosition;
import com.example.orbitleans.orbit.service.SatellitePassService;
import com.example.orbitleans.orbit.service.SatellitePositionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/satellites")
public class SatelliteController {

    private final SatelliteService satelliteService;
    private final SatellitePositionService positionService;
    private final SatellitePassService passService;
    private final Clock clock;

    public SatelliteController(SatelliteService satelliteService, SatellitePositionService positionService,
            SatellitePassService passService, Clock clock) {
        this.satelliteService = satelliteService;
        this.positionService = positionService;
        this.passService = passService;
        this.clock = clock;
    }

    @GetMapping("/{catalogNumber}")
    public SatelliteResponse findByCatalogNumber(
            @PathVariable long catalogNumber
    ) {
        return satelliteService.findByCatalogNumber(catalogNumber);
    }

    @GetMapping("/{catalogNumber}/position")
    public SatellitePositionResponse position(@PathVariable long catalogNumber,
            @RequestParam(required = false) Instant at) {
        SatelliteResponse satellite = satelliteService.findByCatalogNumber(catalogNumber);
        SatellitePosition position = positionService.calculatePosition(
                satellite, at == null ? clock.instant() : at);
        return new SatellitePositionResponse(catalogNumber, satellite.name(), position.instant(),
                position.latitudeDegrees(), position.longitudeDegrees(), position.altitudeKm());
    }

    @GetMapping("/{catalogNumber}/passes")
    public List<SatellitePass> passes(@PathVariable long catalogNumber,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "0") double altitudeMeters,
            @RequestParam(defaultValue = "24") int hours,
            @RequestParam(defaultValue = "0") double minimumElevation,
            @RequestParam(required = false) Instant from) {
        SatelliteResponse satellite = satelliteService.findByCatalogNumber(catalogNumber);
        return passService.findPasses(satellite,
                new ObserverLocation(latitude, longitude, altitudeMeters),
                from == null ? clock.instant() : from,
                Duration.ofHours(hours), minimumElevation);
    }
}
