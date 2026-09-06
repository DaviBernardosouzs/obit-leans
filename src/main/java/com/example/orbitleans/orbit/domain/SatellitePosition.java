package com.example.orbitleans.orbit.domain;

import java.time.Instant;

public record SatellitePosition(
        Instant instant,
        double latitudeDegrees,
        double longitudeDegrees,
        double altitudeKm
) {
}
