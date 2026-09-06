package com.example.orbitleans.imagery.service;

import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.service.GeocodingService;
import com.example.orbitleans.geospatial.domain.BoundingBox;
import com.example.orbitleans.geospatial.service.BoundingBoxCalculator;
import com.example.orbitleans.imagery.client.nasa.NasaGibsClient;
import com.example.orbitleans.imagery.client.nasa.dto.NasaGibsRequest;
import com.example.orbitleans.imagery.dto.ImageryRequest;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;

@Service
public class ImageryService {

    private final NasaGibsClient nasaGibsClient;
    private final GeocodingService geocodingService;
    private final BoundingBoxCalculator boundingBoxCalculator;
    private final int maxDimension;
    private final double boundingBoxRadiusDegrees;

    public ImageryService(
            NasaGibsClient nasaGibsClient,
            GeocodingService geocodingService,
            BoundingBoxCalculator boundingBoxCalculator,
            @Value("${imagery.max-dimension}") int maxDimension,
            @Value("${imagery.bounding-box-radius-degrees}") double boundingBoxRadiusDegrees
    ) {
        this.nasaGibsClient = nasaGibsClient;
        this.geocodingService = geocodingService;
        this.boundingBoxCalculator = boundingBoxCalculator;
        this.maxDimension = maxDimension;
        this.boundingBoxRadiusDegrees = boundingBoxRadiusDegrees;
    }

    public byte[] getImage(ImageryRequest request) {

        validateRequest(request);

        LocationResponse location =
                geocodingService.geocode(request.address());

        BoundingBox boundingBox = boundingBoxCalculator.calculate(
                location.latitude(),
                location.longitude(),
                boundingBoxRadiusDegrees
        );

        NasaGibsRequest nasaRequest = new NasaGibsRequest(
                request.layer(),
                boundingBox.toWmsString(),
                request.width(),
                request.height(),
                request.time(),
                request.transparent()
        );

        return nasaGibsClient.getImage(nasaRequest);
    }

    private void validateRequest(ImageryRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Image request cannot be null"
            );
        }

        if (request.address() == null || request.address().isBlank()) {
            throw new IllegalArgumentException(
                    "Address cannot be empty"
            );
        }

        if (request.layer() == null || request.layer().isBlank()) {
            throw new IllegalArgumentException(
                    "Layer cannot be empty"
            );
        }

        if (request.width() <= 0 || request.height() <= 0) {
            throw new IllegalArgumentException(
                    "Width and height must be greater than zero"
            );
        }
        if (request.width() > maxDimension || request.height() > maxDimension) {
            throw new IllegalArgumentException("Width and height cannot exceed " + maxDimension);
        }

        if (request.time() == null) {
            throw new IllegalArgumentException(
                    "Time cannot be null"
            );
        }

        if (request.time().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Time cannot be in the future"
            );
        }
    }
}
