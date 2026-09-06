package com.example.orbitleans.orbit.service;

import com.example.orbitleans.config.OrekitConfig;
import com.example.orbitleans.orbit.domain.ObserverLocation;
import com.example.orbitleans.orbit.domain.OrbitElements;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SatellitePassServiceTest {
    @BeforeAll
    static void configureOrekitData() {
        new OrekitConfig();
    }

    @Test
    void explicitlyReturnsNoPassWhenThresholdIsNotReached() {
        SatellitePassService service = new SatellitePassService(new SatellitePositionService(), 168);
        assertThat(service.findPasses(vanguard(), new ObserverLocation(0, 0, 0),
                Instant.parse("2000-06-27T18:50:19.733568Z"), Duration.ofMinutes(1), 89)).isEmpty();
    }

    @Test
    void validatesWindowAndObserverInputs() {
        SatellitePassService service = new SatellitePassService(new SatellitePositionService(), 168);
        assertThatThrownBy(() -> service.findPasses(vanguard(), new ObserverLocation(0, 0, 0),
                Instant.EPOCH, Duration.ofHours(169), 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new ObserverLocation(91, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }

    private SatelliteResponse vanguard() {
        Instant epoch = Instant.parse("2000-06-27T18:50:19.733568Z");
        return new SatelliteResponse(5, "1958-002B", "VANGUARD 1",
                new OrbitElements(epoch, 34.2682, 0.1849677, 10.82419157, 331.5174,
                        331.7664, 19.3264, 0.00000046, 0, 0.000028098, 0, 475, 41366),
                133, 8632, 2261);
    }
}
