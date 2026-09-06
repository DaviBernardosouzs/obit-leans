package com.example.orbitleans.orbit.domain;

import java.time.Instant;

public record OrbitElements(
        Instant epoch,
        double inclinationDegrees,
        double eccentricity,
        double meanMotionRevolutionsPerDay,
        double rightAscensionAscendingNodeDegrees,
        double argumentOfPericenterDegrees,
        double meanAnomalyDegrees,
        double meanMotionDotRevolutionsPerDaySquared,
        double meanMotionDoubleDotRevolutionsPerDayCubed,
        double bStar,
        int ephemerisType,
        int elementSetNumber,
        int revolutionAtEpoch
) {
}
