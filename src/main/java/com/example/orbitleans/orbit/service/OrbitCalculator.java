package com.example.orbitleans.orbit.service;

import org.springframework.stereotype.Service;

@Service
public class OrbitCalculator {

    private static final double MINUTES_PER_DAY = 1440.0;
    private static final double SECONDS_PER_DAY = 86_400.0;
    private static final double EARTH_GRAVITATIONAL_PARAMETER_KM_CUBED_PER_SECOND_SQUARED = 398_600.4418;
    private static final double MEAN_EARTH_RADIUS_KM = 6_371.0088;

    public double calculateOrbitalPeriodMinutes(double meanMotion) {

        validateMeanMotion(meanMotion);
        return MINUTES_PER_DAY / meanMotion;
    }

    public double calculateSemiMajorAxisKm(double meanMotion) {
        validateMeanMotion(meanMotion);
        double angularVelocityRadiansPerSecond = meanMotion * 2.0 * Math.PI / SECONDS_PER_DAY;
        return Math.cbrt(EARTH_GRAVITATIONAL_PARAMETER_KM_CUBED_PER_SECOND_SQUARED
                / (angularVelocityRadiansPerSecond * angularVelocityRadiansPerSecond));
    }

    public double calculateApproximateAltitudeKm(double meanMotion) {
        return calculateSemiMajorAxisKm(meanMotion) - MEAN_EARTH_RADIUS_KM;
    }

    private void validateMeanMotion(double meanMotion) {
        if (!Double.isFinite(meanMotion) || meanMotion <= 0) {
            throw new IllegalArgumentException("Mean motion must be greater than zero");
        }
    }
}
