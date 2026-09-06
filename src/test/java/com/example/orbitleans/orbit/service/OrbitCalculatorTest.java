package com.example.orbitleans.orbit.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrbitCalculatorTest {
    private final OrbitCalculator calculator = new OrbitCalculator();

    @Test
    void calculatesIssLikeMetricsWithoutRounding() {
        double meanMotion = 15.5;
        assertThat(calculator.calculateOrbitalPeriodMinutes(meanMotion)).isEqualTo(1440.0 / meanMotion);
        assertThat(calculator.calculateSemiMajorAxisKm(meanMotion)).isBetween(6_790.0, 6_800.0);
        assertThat(calculator.calculateApproximateAltitudeKm(meanMotion)).isBetween(419.0, 429.0);
    }

    @Test
    void rejectsNonPositiveAndNonFiniteMeanMotion() {
        for (double value : new double[]{0, -1, Double.NaN, Double.POSITIVE_INFINITY}) {
            assertThatThrownBy(() -> calculator.calculateOrbitalPeriodMinutes(value))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
