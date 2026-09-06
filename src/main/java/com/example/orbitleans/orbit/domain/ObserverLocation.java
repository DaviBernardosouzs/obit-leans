package com.example.orbitleans.orbit.domain;

public record ObserverLocation(
        double latitudeDegrees,
        double longitudeDegrees,
        double altitudeMeters
) {
    public ObserverLocation {
        if (!Double.isFinite(latitudeDegrees) || latitudeDegrees < -90 || latitudeDegrees > 90) {
            throw new IllegalArgumentException("Latitude must be between -90 and 90");
        }
        if (!Double.isFinite(longitudeDegrees) || longitudeDegrees < -180 || longitudeDegrees > 180) {
            throw new IllegalArgumentException("Longitude must be between -180 and 180");
        }
        if (!Double.isFinite(altitudeMeters) || altitudeMeters < -500 || altitudeMeters > 10_000) {
            throw new IllegalArgumentException("Observer altitude must be between -500 and 10000 meters");
        }
    }
}
