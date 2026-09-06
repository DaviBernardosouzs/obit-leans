package com.example.orbitleans.geocoding.controller;

import com.example.orbitleans.exception.ApiExceptionHandler;
import com.example.orbitleans.exception.ExternalServiceException;
import com.example.orbitleans.exception.LocationNotFoundException;
import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.service.GeocodingService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class GeocodingControllerTest {
    @Test
    void returnsLocationAndMapsDomainErrors() throws Exception {
        GeocodingService service = mock(GeocodingService.class);
        when(service.geocode("Ipatinga")).thenReturn(new LocationResponse(-19.4, -42.5, "Ipatinga"));
        when(service.geocode("missing")).thenThrow(new LocationNotFoundException("Location not found"));
        when(service.geocode("down")).thenThrow(new ExternalServiceException("Nominatim service is unavailable"));
        MockMvc mvc = standaloneSetup(new GeocodingController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();

        mvc.perform(get("/api/geocoding").param("address", "Ipatinga"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.displayName").value("Ipatinga"));
        mvc.perform(get("/api/geocoding").param("address", "missing"))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/geocoding").param("address", "down"))
                .andExpect(status().isBadGateway()).andExpect(content().string("Nominatim service is unavailable"));
    }
}
