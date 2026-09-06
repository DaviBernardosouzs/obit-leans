package com.example.orbitleans.geocoding.service;

import com.example.orbitleans.exception.LocationNotFoundException;
import com.example.orbitleans.geocoding.client.NominatimClient;
import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.dto.NominatimResponse;

import org.springframework.stereotype.Service;


@Service
public class GeocodingService {

    private final NominatimClient nominatimClient;

    public GeocodingService(NominatimClient nominatimClient) {
        this.nominatimClient = nominatimClient;
    }


    public LocationResponse geocode(String address) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException("Address cannot be empty");
        }
        NominatimResponse[] responses = nominatimClient.search(address);
        if (responses == null || responses.length == 0) {
            throw new LocationNotFoundException("Location not found");
        }

        NominatimResponse result = responses[0];

        double latitude;
        double longitude;
        try {
            latitude = Double.parseDouble(result.lat());
            longitude = Double.parseDouble(result.lon());
        } catch (NumberFormatException | NullPointerException exception) {
            throw new com.example.orbitleans.exception.ExternalServiceException(
                    "Nominatim returned invalid coordinates", exception);
        }
        if (!Double.isFinite(latitude) || latitude < -90 || latitude > 90
                || !Double.isFinite(longitude) || longitude < -180 || longitude > 180) {
            throw new com.example.orbitleans.exception.ExternalServiceException(
                    "Nominatim returned invalid coordinates");
        }

        return new LocationResponse(
                latitude,
                longitude,
                result.displayname()
        );


    }


}
