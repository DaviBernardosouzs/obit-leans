package com.example.orbitleans.geocoding.service;

import com.example.orbitleans.exception.ExternalServiceException;
import com.example.orbitleans.exception.LocationNotFoundException;
import com.example.orbitleans.geocoding.client.NominatimClient;
import com.example.orbitleans.geocoding.dto.NominatimResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GeocodingServiceTest {
    private final NominatimClient client = mock(NominatimClient.class);
    private final GeocodingService service = new GeocodingService(client);

    @Test
    void mapsExternalCoordinatesAndDisplayName() {
        when(client.search("Ipatinga")).thenReturn(new NominatimResponse[]{
                new NominatimResponse("-19.4777807", "-42.5270802", "Ipatinga, MG")});
        assertThat(service.geocode("Ipatinga")).satisfies(location -> {
            assertThat(location.latitude()).isEqualTo(-19.4777807);
            assertThat(location.longitude()).isEqualTo(-42.5270802);
            assertThat(location.displayName()).isEqualTo("Ipatinga, MG");
        });
    }

    @Test
    void handlesInvalidInputEmptyResultAndMalformedCoordinates() {
        assertThatThrownBy(() -> service.geocode(" ")).isInstanceOf(IllegalArgumentException.class);
        when(client.search("missing")).thenReturn(new NominatimResponse[0]);
        assertThatThrownBy(() -> service.geocode("missing")).isInstanceOf(LocationNotFoundException.class);
        when(client.search("bad")).thenReturn(new NominatimResponse[]{new NominatimResponse("x", "1", "bad")});
        assertThatThrownBy(() -> service.geocode("bad")).isInstanceOf(ExternalServiceException.class);
    }
}
