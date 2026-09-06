package com.example.orbitleans.orbit.service;

import com.example.orbitleans.orbit.domain.ObserverLocation;
import com.example.orbitleans.orbit.domain.SatellitePass;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class SatellitePassService {
    private static final Duration SEARCH_STEP = Duration.ofSeconds(30);
    private final SatellitePositionService positionService;
    private final int maxHours;

    public SatellitePassService(SatellitePositionService positionService,
            @Value("${passes.max-hours}") int maxHours) {
        this.positionService = positionService;
        this.maxHours = maxHours;
    }

    public List<SatellitePass> findPasses(SatelliteResponse satellite, ObserverLocation observer,
            Instant start, Duration window, double minimumElevationDegrees) {
        validate(start, window, minimumElevationDegrees);
        TLEPropagator propagator = positionService.createPropagator(satellite);
        TopocentricFrame topocentricFrame = new TopocentricFrame(
                positionService.earth(),
                new GeodeticPoint(Math.toRadians(observer.latitudeDegrees()),
                        Math.toRadians(observer.longitudeDegrees()), observer.altitudeMeters()),
                "observer");
        Instant end = start.plus(window);
        List<SatellitePass> passes = new ArrayList<>();
        Instant previous = start;
        double previousElevation = elevation(propagator, topocentricFrame, previous);
        Instant rise = previousElevation >= minimumElevationDegrees ? start : null;

        for (Instant current = start.plus(SEARCH_STEP); !current.isAfter(end); current = current.plus(SEARCH_STEP)) {
            double currentElevation = elevation(propagator, topocentricFrame, current);
            if (rise == null && previousElevation < minimumElevationDegrees
                    && currentElevation >= minimumElevationDegrees) {
                rise = refineCrossing(propagator, topocentricFrame, previous, current,
                        minimumElevationDegrees, true);
            } else if (rise != null && previousElevation >= minimumElevationDegrees
                    && currentElevation < minimumElevationDegrees) {
                Instant set = refineCrossing(propagator, topocentricFrame, previous, current,
                        minimumElevationDegrees, false);
                passes.add(buildPass(propagator, topocentricFrame, rise, set));
                rise = null;
            }
            previous = current;
            previousElevation = currentElevation;
        }
        if (rise != null) {
            passes.add(buildPass(propagator, topocentricFrame, rise, end));
        }
        return List.copyOf(passes);
    }

    private SatellitePass buildPass(TLEPropagator propagator, TopocentricFrame observer,
            Instant rise, Instant set) {
        Instant maximumTime = rise;
        double maximum = elevation(propagator, observer, rise);
        for (Instant candidate = rise.plusSeconds(10); candidate.isBefore(set); candidate = candidate.plusSeconds(10)) {
            double candidateElevation = elevation(propagator, observer, candidate);
            if (candidateElevation > maximum) {
                maximum = candidateElevation;
                maximumTime = candidate;
            }
        }
        return new SatellitePass(rise, maximumTime, set, maximum);
    }

    private Instant refineCrossing(TLEPropagator propagator, TopocentricFrame observer,
            Instant low, Instant high, double threshold, boolean rising) {
        while (Duration.between(low, high).toMillis() > 250) {
            Instant middle = low.plusMillis(Duration.between(low, high).toMillis() / 2);
            boolean above = elevation(propagator, observer, middle) >= threshold;
            if (above == rising) {
                high = middle;
            } else {
                low = middle;
            }
        }
        return high;
    }

    private double elevation(TLEPropagator propagator, TopocentricFrame observer, Instant instant) {
        AbsoluteDate date = positionService.toAbsoluteDate(instant);
        Vector3D position = propagator.getPVCoordinates(date).getPosition();
        return Math.toDegrees(observer.getElevation(position, propagator.getFrame(), date));
    }

    private void validate(Instant start, Duration window, double minimumElevationDegrees) {
        if (start == null || window == null) {
            throw new IllegalArgumentException("Start instant and search window are required");
        }
        if (window.isZero() || window.isNegative() || window.compareTo(Duration.ofHours(maxHours)) > 0) {
            throw new IllegalArgumentException("Search window must be between 1 second and " + maxHours + " hours");
        }
        if (!Double.isFinite(minimumElevationDegrees)
                || minimumElevationDegrees < 0 || minimumElevationDegrees >= 90) {
            throw new IllegalArgumentException("Minimum elevation must be between 0 and 90 degrees");
        }
    }
}
