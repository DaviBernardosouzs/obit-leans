package com.example.orbitleans.geospatial.domain;

public record BoundingBox(

        double minLongitude,
        double minLatitude,
        double maxLongitude,
        double maxLatitude

) {

    public String toWmsString() {
        return minLongitude + ","
                + minLatitude + ","
                + maxLongitude + ","
                + maxLatitude;
    }

}
