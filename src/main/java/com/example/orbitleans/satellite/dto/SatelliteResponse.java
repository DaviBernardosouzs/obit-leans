package com.example.orbitleans.satellite.dto;

import com.example.orbitleans.orbit.domain.OrbitElements;

public record SatelliteResponse(
        long catalogNumber,
        String objectId,
        String name,
        OrbitElements orbitElements,
        double orbitalPeriodMinutes,
        double semiMajorAxisKm,
        double approximateAltitudeKm
) {


}
