package com.example.orbitleans.geospatial.service;

import com.example.orbitleans.geospatial.domain.BoundingBox;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BoundingBoxCalculatorTest {
    private final BoundingBoxCalculator calculator = new BoundingBoxCalculator();

    @Test
    void preservesWms111LongitudeLatitudeOrder() {
        BoundingBox box = calculator.calculate(-19.5, -42.5, 0.15);
        assertThat(box.toWmsString()).isEqualTo("-42.65,-19.65,-42.35,-19.35");
    }

    @Test
    void clampsCoordinatesAtPolesAndAntimeridian() {
        assertThat(calculator.calculate(89.9, 179.9, 1))
                .isEqualTo(new BoundingBox(178.9, 88.9, 180, 90));
    }

    @Test
    void rejectsInvalidCoordinatesAndRadius() {
        assertThatThrownBy(() -> calculator.calculate(91, 0, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> calculator.calculate(0, 181, 1)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> calculator.calculate(0, 0, 0)).isInstanceOf(IllegalArgumentException.class);
    }
}
