package com.example.orbitleans.satellite.controller;

import com.example.orbitleans.exception.ApiExceptionHandler;
import com.example.orbitleans.orbit.domain.OrbitElements;
import com.example.orbitleans.orbit.domain.SatellitePass;
import com.example.orbitleans.orbit.domain.SatellitePosition;
import com.example.orbitleans.orbit.service.SatellitePassService;
import com.example.orbitleans.orbit.service.SatellitePositionService;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import com.example.orbitleans.satellite.service.SatelliteService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class SatelliteControllerTest {
    @Test
    void servesSatellitePositionAndPassesWithFixedTime() throws Exception {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        SatelliteResponse satellite = satellite(now);
        SatelliteService satellites = mock(SatelliteService.class);
        SatellitePositionService positions = mock(SatellitePositionService.class);
        SatellitePassService passes = mock(SatellitePassService.class);
        when(satellites.findByCatalogNumber(25544)).thenReturn(satellite);
        when(positions.calculatePosition(satellite, now)).thenReturn(new SatellitePosition(now, 1, 2, 420));
        when(passes.findPasses(eq(satellite), any(), eq(now), any(), anyDouble())).thenReturn(
                List.of(new SatellitePass(now, now.plusSeconds(60), now.plusSeconds(120), 45)));
        MockMvc mvc = standaloneSetup(new SatelliteController(satellites, positions, passes,
                Clock.fixed(now, ZoneOffset.UTC))).setControllerAdvice(new ApiExceptionHandler()).build();

        mvc.perform(get("/api/satellites/25544")).andExpect(status().isOk())
                .andExpect(jsonPath("$.objectId").value("1998-067A"));
        mvc.perform(get("/api/satellites/25544/position")).andExpect(status().isOk())
                .andExpect(jsonPath("$.altitudeKm").value(420));
        mvc.perform(get("/api/satellites/25544/passes").param("latitude", "-19.4")
                        .param("longitude", "-42.5"))
                .andExpect(status().isOk()).andExpect(jsonPath("$[0].maximumElevationDegrees").value(45));
    }

    public static SatelliteResponse satellite(Instant epoch) {
        OrbitElements elements = new OrbitElements(epoch, 51.6, .0005, 15.5, 20, 30, 40,
                .0001, 0, .0002, 0, 999, 12345);
        return new SatelliteResponse(25544, "1998-067A", "ISS (ZARYA)", elements, 92.9, 6795, 424);
    }
}
