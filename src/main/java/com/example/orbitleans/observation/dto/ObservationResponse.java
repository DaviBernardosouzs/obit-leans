package com.example.orbitleans.observation.dto;

import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.orbit.domain.SatellitePass;
import com.example.orbitleans.satellite.dto.SatellitePositionResponse;
import com.example.orbitleans.satellite.dto.SatelliteResponse;

import java.util.Optional;

public record ObservationResponse(
        LocationResponse location,
        SatelliteResponse satellite,
        SatellitePositionResponse currentPosition,
        Optional<SatellitePass> nextPass,
        ImageryLink imagery
) {
}
