package com.example.orbitleans.satellite.domain;

import com.example.orbitleans.orbit.domain.OrbitElements;

public record Satellite(

        long catalogNumber,
        String objectId,
        String name,
        OrbitElements orbitElements
) {
}