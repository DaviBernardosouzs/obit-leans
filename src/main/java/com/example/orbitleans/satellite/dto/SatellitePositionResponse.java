package com.example.orbitleans.satellite.dto;

import java.time.Instant;

public record SatellitePositionResponse(
        long catalogNumber,
        String name,
        Instant instant,
        double latitudeDegrees,
        double longitudeDegrees,
        double altitudeKm
) {
}
