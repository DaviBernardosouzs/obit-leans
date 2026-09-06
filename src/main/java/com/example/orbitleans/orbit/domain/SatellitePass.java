package com.example.orbitleans.orbit.domain;

import java.time.Instant;

public record SatellitePass(
        Instant riseTime,
        Instant maximumElevationTime,
        Instant setTime,
        double maximumElevationDegrees
) {
}
