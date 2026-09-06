package com.example.orbitleans.observation.service;

import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.service.GeocodingService;
import com.example.orbitleans.observation.dto.ImageryLink;
import com.example.orbitleans.observation.dto.ObservationResponse;
import com.example.orbitleans.orbit.domain.ObserverLocation;
import com.example.orbitleans.orbit.domain.SatellitePass;
import com.example.orbitleans.orbit.domain.SatellitePosition;
import com.example.orbitleans.orbit.service.SatellitePassService;
import com.example.orbitleans.orbit.service.SatellitePositionService;
import com.example.orbitleans.satellite.dto.SatellitePositionResponse;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import com.example.orbitleans.satellite.service.SatelliteService;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class ObservationService {
    private static final String DEFAULT_IMAGERY_LAYER = "MODIS_Terra_CorrectedReflectance_TrueColor";
    private final GeocodingService geocodingService;
    private final SatelliteService satelliteService;
    private final SatellitePositionService positionService;
    private final SatellitePassService passService;
    private final Clock clock;

    public ObservationService(GeocodingService geocodingService, SatelliteService satelliteService,
            SatellitePositionService positionService, SatellitePassService passService, Clock clock) {
        this.geocodingService = geocodingService;
        this.satelliteService = satelliteService;
        this.positionService = positionService;
        this.passService = passService;
        this.clock = clock;
    }

    public ObservationResponse observe(String address, long catalogNumber) {
        LocationResponse location = geocodingService.geocode(address);
        SatelliteResponse satellite = satelliteService.findByCatalogNumber(catalogNumber);
        Instant now = clock.instant();
        SatellitePosition position = positionService.calculatePosition(satellite, now);
        SatellitePositionResponse currentPosition = new SatellitePositionResponse(
                satellite.catalogNumber(), satellite.name(), position.instant(), position.latitudeDegrees(),
                position.longitudeDegrees(), position.altitudeKm());
        List<SatellitePass> passes = passService.findPasses(satellite,
                new ObserverLocation(location.latitude(), location.longitude(), 0),
                now, Duration.ofHours(24), 0);
        String imageryUrl = UriComponentsBuilder.fromPath("/api/imagery")
                .queryParam("address", address)
                .queryParam("layer", DEFAULT_IMAGERY_LAYER)
                .queryParam("width", 800)
                .queryParam("height", 800)
                .queryParam("time", LocalDate.ofInstant(now, ZoneOffset.UTC))
                .queryParam("transparent", false)
                .build().encode().toUriString();
        return new ObservationResponse(location, satellite, currentPosition,
                passes.stream().findFirst(),
                new ImageryLink(imageryUrl, "NASA GIBS Earth observation imagery"));
    }
}
