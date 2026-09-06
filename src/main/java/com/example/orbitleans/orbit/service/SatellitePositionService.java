package com.example.orbitleans.orbit.service;

import com.example.orbitleans.orbit.domain.OrbitElements;
import com.example.orbitleans.orbit.domain.SatellitePosition;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SatellitePositionService {
    private static final Pattern OBJECT_ID = Pattern.compile("(\\d{4})-(\\d{3})([A-Z0-9]+)");
    private static final double SECONDS_PER_DAY = 86_400.0;
    private static final TimeScale UTC = TimeScalesFactory.getUTC();
    private static final Frame EARTH_FRAME = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
    private static final OneAxisEllipsoid EARTH = new OneAxisEllipsoid(
            Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
            Constants.WGS84_EARTH_FLATTENING,
            EARTH_FRAME);

    public SatellitePosition calculatePosition(SatelliteResponse satellite, Instant instant) {
        if (satellite == null || instant == null) {
            throw new IllegalArgumentException("Satellite and instant are required");
        }
        TLEPropagator propagator = createPropagator(satellite);
        AbsoluteDate date = toAbsoluteDate(instant);
        Vector3D position = propagator.getPVCoordinates(date).getPosition();
        GeodeticPoint point = EARTH.transform(position, propagator.getFrame(), date);
        return new SatellitePosition(
                instant,
                Math.toDegrees(point.getLatitude()),
                Math.toDegrees(point.getLongitude()),
                point.getAltitude() / 1_000.0);
    }

    public TLEPropagator createPropagator(SatelliteResponse satellite) {
        OrbitElements elements = satellite.orbitElements();
        Matcher objectId = OBJECT_ID.matcher(satellite.objectId() == null ? "" : satellite.objectId());
        if (!objectId.matches()) {
            throw new IllegalArgumentException("Satellite objectId must use YYYY-NNNP format");
        }
        int launchYear = Integer.parseInt(objectId.group(1));
        int launchNumber = Integer.parseInt(objectId.group(2));
        String launchPiece = objectId.group(3);
        TLE tle = new TLE(
                Math.toIntExact(satellite.catalogNumber()), 'U', launchYear, launchNumber, launchPiece,
                elements.ephemerisType(), elements.elementSetNumber(), toAbsoluteDate(elements.epoch()),
                revolutionsPerDayToRadiansPerSecond(elements.meanMotionRevolutionsPerDay()),
                elements.meanMotionDotRevolutionsPerDaySquared() * 2.0 * Math.PI
                        / (SECONDS_PER_DAY * SECONDS_PER_DAY),
                elements.meanMotionDoubleDotRevolutionsPerDayCubed() * 2.0 * Math.PI
                        / (SECONDS_PER_DAY * SECONDS_PER_DAY * SECONDS_PER_DAY),
                elements.eccentricity(), Math.toRadians(elements.inclinationDegrees()),
                Math.toRadians(elements.argumentOfPericenterDegrees()),
                Math.toRadians(elements.rightAscensionAscendingNodeDegrees()),
                Math.toRadians(elements.meanAnomalyDegrees()), elements.revolutionAtEpoch(), elements.bStar(), UTC);
        return TLEPropagator.selectExtrapolator(tle);
    }

    public AbsoluteDate toAbsoluteDate(Instant instant) {
        return new AbsoluteDate(Date.from(instant), UTC);
    }

    public OneAxisEllipsoid earth() {
        return EARTH;
    }

    private double revolutionsPerDayToRadiansPerSecond(double value) {
        return value * 2.0 * Math.PI / SECONDS_PER_DAY;
    }
}
