package com.example.orbitleans.satellite.service;

import com.example.orbitleans.exception.SatelliteNotFoundException;
import com.example.orbitleans.orbit.service.OrbitCalculator;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakClient;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SatelliteServiceTest {
    private final CelesTrakClient client = mock(CelesTrakClient.class);
    private final SatelliteService service = new SatelliteService(client, new OrbitCalculator());

    @Test
    void mapsIssNameAndObjectIdInCorrectOrderAndMetrics() {
        when(client.findByCatalogNumber(25544)).thenReturn(new CelesTrakResponse[]{iss()});
        assertThat(service.findByCatalogNumber(25544)).satisfies(response -> {
            assertThat(response.catalogNumber()).isEqualTo(25544);
            assertThat(response.objectId()).isEqualTo("1998-067A");
            assertThat(response.name()).isEqualTo("ISS (ZARYA)");
            assertThat(response.orbitalPeriodMinutes()).isPositive();
            assertThat(response.semiMajorAxisKm()).isGreaterThan(response.approximateAltitudeKm());
            assertThat(response.orbitElements().epoch().toString()).isEqualTo("2024-01-01T00:00:00Z");
        });
    }

    @Test
    void rejectsCatalogAndHandlesEmptyExternalArray() {
        assertThatThrownBy(() -> service.findByCatalogNumber(0)).isInstanceOf(IllegalArgumentException.class);
        when(client.findByCatalogNumber(9)).thenReturn(new CelesTrakResponse[0]);
        assertThatThrownBy(() -> service.findByCatalogNumber(9)).isInstanceOf(SatelliteNotFoundException.class);
    }

    static CelesTrakResponse iss() {
        return new CelesTrakResponse("ISS (ZARYA)", "1998-067A", 25544,
                "2024-01-01T00:00:00", 51.64, 0.0005, 15.5, 20, 30, 40,
                0.0001, 0, 0, 999, 12345, 0.0002);
    }
}
