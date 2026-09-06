
package com.example.orbitleans.imagery.client.nasa;

import com.example.orbitleans.imagery.client.nasa.dto.NasaGibsRequest;
import com.example.orbitleans.exception.ExternalServiceException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class NasaGibsClient {

    private final RestClient restClient;

    public NasaGibsClient(
            @Qualifier("nasaGibsRestClient") RestClient restClient
    ) {
        this.restClient = restClient;
    }

    public byte[] getImage(NasaGibsRequest request) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/wms/epsg4326/best/wms.cgi")
                            .queryParam("SERVICE", "WMS")
                            .queryParam("REQUEST", "GetMap")
                            .queryParam("VERSION", "1.1.1")
                            .queryParam("LAYERS", request.layer())
                            .queryParam("STYLES", "")
                            .queryParam("SRS", "EPSG:4326")
                            .queryParam("BBOX", request.bbox())
                            .queryParam("WIDTH", request.width())
                            .queryParam("HEIGHT", request.height())
                            .queryParam("FORMAT", "image/png")
                            .queryParam("TIME", request.time())
                            .queryParam("TRANSPARENT", request.transparent())
                            .build())
                    .accept(MediaType.IMAGE_PNG)
                    .exchange((httpRequest, response) -> {
                        if (response.getStatusCode().isError()) {
                            throw new ExternalServiceException("NASA GIBS rejected the imagery request");
                        }
                        MediaType contentType = response.getHeaders().getContentType();
                        if (contentType == null || !MediaType.IMAGE_PNG.isCompatibleWith(contentType)) {
                            throw new ExternalServiceException("NASA GIBS returned unexpected content");
                        }
                        byte[] body = response.getBody().readAllBytes();
                        if (body.length == 0 || isXml(body)) {
                            throw new ExternalServiceException("NASA GIBS returned an invalid image");
                        }
                        return body;
                    });
        } catch (ExternalServiceException exception) {
            throw exception;
        } catch (RestClientException exception) {
            throw new ExternalServiceException("NASA GIBS service is unavailable", exception);
        }
    }

    private boolean isXml(byte[] body) {
        int length = Math.min(body.length, 100);
        String prefix = new String(body, 0, length, java.nio.charset.StandardCharsets.UTF_8).stripLeading();
        return prefix.startsWith("<?xml") || prefix.startsWith("<ServiceException");
    }
}
