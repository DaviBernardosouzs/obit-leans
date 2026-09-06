package com.example.orbitleans.geospatial.service;


import com.example.orbitleans.geospatial.domain.BoundingBox;
import org.springframework.stereotype.Service;

@Service
public class BoundingBoxCalculator {


    public BoundingBox calculate(
            double latitude,
            double longitude,
            double radius
    ) {

        if (!Double.isFinite(radius) || radius <= 0 || radius > 180) {
            throw new IllegalArgumentException("Radius must be greater than zero");
        }
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90){
            throw  new IllegalArgumentException("Latitude must be between -90 and 90");
        }


        if (!Double.isFinite(longitude) || longitude < -180 || longitude > 180){
            throw  new IllegalArgumentException("Longitude must be between -180 and 180");
        }


        double minLongitude = Math.max(-180,longitude - radius);
        double minLatitude = Math.max(-90,latitude - radius);
        double maxLatitude = Math.min(90,latitude + radius);
        double maxLongitude = Math.min(180,longitude + radius);

        return new BoundingBox(

                minLongitude,
                minLatitude,
                maxLongitude,
                maxLatitude
        );
    }


}
