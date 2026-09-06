package com.example.orbitleans.orbit.service;

import com.example.orbitleans.config.OrekitConfig;
import com.example.orbitleans.orbit.domain.OrbitElements;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.time.TimeScalesFactory;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class SatellitePositionServiceTest {
    @BeforeAll
    static void configureOrekitData() {
        new OrekitConfig();
    }

    @Test
    void matchesValladoSgp4VerificationPositionAtEpoch() {
        Instant epoch = Instant.parse("2000-06-27T18:50:19.733568Z");
        OrbitElements elements = new OrbitElements(epoch, 34.2682, 0.1849677,
                10.82419157, 331.5174, 331.7664, 19.3264,
                0.00000046, 0, 0.000028098, 0, 475, 41366);
        SatelliteResponse satellite = new SatelliteResponse(5, "1958-002B", "VANGUARD 1",
                elements, 0, 0, 0);
        SatellitePositionService service = new SatellitePositionService();
        TLEPropagator propagator = service.createPropagator(satellite);
        Vector3D positionKm = propagator.getPVCoordinates(service.toAbsoluteDate(epoch))
                .getPosition().scalarMultiply(0.001);

        TLE publishedReference = new TLE(
                "1 00005U 58002B   00179.78495062  .00000023  00000-0  28098-4 0  4753",
                "2 00005  34.2682 331.5174 1849677 331.7664  19.3264 10.82419157413667",
                TimeScalesFactory.getUTC());
        Vector3D referenceKm = TLEPropagator.selectExtrapolator(publishedReference)
                .getPVCoordinates(publishedReference.getDate()).getPosition().scalarMultiply(0.001);

        // Vallado SGP4 verification case 00005; OMM conversion must remain within 20 m.
        assertThat(positionKm.getX()).isCloseTo(referenceKm.getX(), within(0.02));
        assertThat(positionKm.getY()).isCloseTo(referenceKm.getY(), within(0.02));
        assertThat(positionKm.getZ()).isCloseTo(referenceKm.getZ(), within(0.02));
    }

    private static org.assertj.core.data.Offset<Double> within(double value) {
        return org.assertj.core.data.Offset.offset(value);
    }
}
