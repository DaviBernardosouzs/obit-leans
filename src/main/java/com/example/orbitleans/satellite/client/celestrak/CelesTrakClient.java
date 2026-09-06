package com.example.orbitleans.satellite.client.celestrak;
import com.example.orbitleans.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class CelesTrakClient {

    private final RestClient restClient;

    public CelesTrakClient(
            @Qualifier("celesTrakRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }
    public CelesTrakResponse[] findByCatalogNumber(long catalogNumber) {
        try {
            CelesTrakResponse[] response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/NORAD/elements/gp.php")
                            .queryParam("CATNR", catalogNumber)
                            .queryParam("FORMAT", "JSON")
                            .build())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(CelesTrakResponse[].class);
            if (response == null) {
                throw new ExternalServiceException("CelesTrak returned an empty response");
            }
            return response;
        } catch (ExternalServiceException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ExternalServiceException("CelesTrak service is unavailable", exception);
        }
    }


}
