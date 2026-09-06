package com.example.orbitleans.observation.controller;

import com.example.orbitleans.observation.dto.ImageryLink;
import com.example.orbitleans.observation.dto.ObservationResponse;
import com.example.orbitleans.observation.service.ObservationService;
import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.satellite.dto.SatellitePositionResponse;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Optional;

import static com.example.orbitleans.satellite.controller.SatelliteControllerTest.satellite;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class ObservationControllerTest {
    @Test
    void returnsIntegratedJsonWithoutBase64Image() throws Exception {
        Instant now = Instant.parse("2024-01-01T00:00:00Z");
        ObservationService service = mock(ObservationService.class);
        when(service.observe("Ipatinga", 25544)).thenReturn(new ObservationResponse(
                new LocationResponse(-19.4, -42.5, "Ipatinga"), satellite(now),
                new SatellitePositionResponse(25544, "ISS (ZARYA)", now, 1, 2, 420),
                Optional.empty(), new ImageryLink("/api/imagery?address=Ipatinga", "imagery")));
        MockMvc mvc = standaloneSetup(new ObservationController(service)).build();
        mvc.perform(get("/api/observations").param("address", "Ipatinga")
                        .param("catalogNumber", "25544"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.location.displayName").value("Ipatinga"))
                .andExpect(jsonPath("$.imagery.relativeUrl").value("/api/imagery?address=Ipatinga"));
    }
}
