package com.example.orbitleans.geocoding.client;


import com.example.orbitleans.geocoding.dto.NominatimResponse;
import com.example.orbitleans.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class NominatimClient {

    private final RestClient restClient;

    public NominatimClient(
            @Qualifier("nominatimRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public NominatimResponse[] search(String address) {
        try {
            NominatimResponse[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search")
                            .queryParam("q", address)
                            .queryParam("format", "jsonv2")
                            .queryParam("limit", 1)
                            .build())
                    .retrieve()
                    .body(NominatimResponse[].class);
            if (response == null) {
                throw new ExternalServiceException("Nominatim returned an empty response");
            }
            return response;
        } catch (ExternalServiceException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Nominatim service is unavailable", exception);
        }
    }

}
