package com.example.orbitleans.imagery.service;

import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.service.GeocodingService;
import com.example.orbitleans.geospatial.service.BoundingBoxCalculator;
import com.example.orbitleans.imagery.client.nasa.NasaGibsClient;
import com.example.orbitleans.imagery.dto.ImageryRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ImageryServiceTest {
    private final NasaGibsClient nasaClient = mock(NasaGibsClient.class);
    private final GeocodingService geocoding = mock(GeocodingService.class);
    private final ImageryService service = new ImageryService(
            nasaClient, geocoding, new BoundingBoxCalculator(), 2048, 0.15);

    @Test
    void orchestratesAddressBoundingBoxAndNasaRequest() {
        byte[] png = {1, 2, 3};
        when(geocoding.geocode("Ipatinga")).thenReturn(new LocationResponse(-19.5, -42.5, "Ipatinga"));
        when(nasaClient.getImage(argThat(request -> request.bbox().equals("-42.65,-19.65,-42.35,-19.35"))))
                .thenReturn(png);
        ImageryRequest request = new ImageryRequest("Ipatinga", "layer", 800, 600,
                LocalDate.of(2024, 1, 1), false);
        assertThat(service.getImage(request)).isSameAs(png);
        verify(nasaClient).getImage(argThat(nasaRequest -> nasaRequest.width() == 800
                && nasaRequest.height() == 600 && nasaRequest.layer().equals("layer")));
    }

    @Test
    void rejectsNullInvalidDimensionsAndFutureDate() {
        assertThatThrownBy(() -> service.getImage(null)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getImage(new ImageryRequest("a", "l", 2049, 1,
                LocalDate.of(2024, 1, 1), false))).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.getImage(new ImageryRequest("a", "l", 1, 1,
                LocalDate.now().plusDays(1), false))).isInstanceOf(IllegalArgumentException.class);
    }
}
