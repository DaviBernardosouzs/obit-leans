package com.example.orbitleans.satellite.service;

import com.example.orbitleans.exception.ExternalServiceException;
import com.example.orbitleans.exception.SatelliteNotFoundException;
import com.example.orbitleans.orbit.domain.OrbitElements;
import com.example.orbitleans.orbit.service.OrbitCalculator;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakClient;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakResponse;
import com.example.orbitleans.satellite.domain.Satellite;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.format.DateTimeParseException;

@Service
public class SatelliteService {
    private final CelesTrakClient celesTrakClient;
    private final OrbitCalculator orbitCalculator;

    public SatelliteService(CelesTrakClient celesTrakClient, OrbitCalculator orbitCalculator) {
        this.celesTrakClient = celesTrakClient;
        this.orbitCalculator = orbitCalculator;
    }

    @Cacheable(cacheNames = "celestrak", key = "#catalogNumber", sync = true)
    public SatelliteResponse findByCatalogNumber(long catalogNumber) {
        if (catalogNumber <= 0) {
            throw new IllegalArgumentException("Catalog number must be greater than zero");
        }
        CelesTrakResponse[] responses = celesTrakClient.findByCatalogNumber(catalogNumber);
        if (responses.length == 0) {
            throw new SatelliteNotFoundException("Satellite not found for catalog number " + catalogNumber);
        }
        CelesTrakResponse response = responses[0];
        OrbitElements orbitElements = new OrbitElements(
                parseEpoch(response.epoch()), response.inclination(), response.eccentricity(),
                response.meanMotion(), response.rightAscensionAscendingNode(),
                response.argumentOfPericenter(), response.meanAnomaly(), response.meanMotionDot(),
                response.meanMotionDoubleDot(), response.bStar(), response.ephemerisType(),
                response.elementSetNumber(), response.revolutionAtEpoch());
        Satellite satellite = new Satellite(
                response.noradCatalogId(), response.objectId(), response.objectName(), orbitElements);
        double meanMotion = response.meanMotion();
        return new SatelliteResponse(
                satellite.catalogNumber(), satellite.objectId(), satellite.name(), satellite.orbitElements(),
                orbitCalculator.calculateOrbitalPeriodMinutes(meanMotion),
                orbitCalculator.calculateSemiMajorAxisKm(meanMotion),
                orbitCalculator.calculateApproximateAltitudeKm(meanMotion));
    }

    private Instant parseEpoch(String epoch) {
        if (epoch == null || epoch.isBlank()) {
            throw new ExternalServiceException("CelesTrak returned an invalid epoch");
        }
        try {
            return Instant.parse(epoch.endsWith("Z") ? epoch : epoch + "Z");
        } catch (DateTimeParseException exception) {
            throw new ExternalServiceException("CelesTrak returned an invalid epoch", exception);
        }
    }
}
