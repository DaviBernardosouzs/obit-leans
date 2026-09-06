package com.example.orbitleans.integration;

import com.example.orbitleans.exception.ExternalServiceException;
import com.example.orbitleans.geocoding.client.NominatimClient;
import com.example.orbitleans.imagery.client.nasa.NasaGibsClient;
import com.example.orbitleans.imagery.client.nasa.dto.NasaGibsRequest;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakClient;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExternalClientsTest {
    @Test
    void nominatimUsesExpectedSearchContractWithoutInternet() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://mock.local");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://mock.local/search?q=Ipatinga&format=jsonv2&limit=1"))
                .andRespond(withSuccess("[{\"lat\":\"-19.4\",\"lon\":\"-42.5\",\"display_name\":\"Ipatinga\"}]",
                        MediaType.APPLICATION_JSON));
        assertThat(new NominatimClient(builder.build()).search("Ipatinga")).hasSize(1);
        server.verify();
    }

    @Test
    void celestrakExplicitlyRequestsJsonAndMapsServerFailure() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://mock.local");
        MockRestServiceServer server = MockRestServiceServer.bindTo(builder).build();
        server.expect(requestTo("https://mock.local/NORAD/elements/gp.php?CATNR=25544&FORMAT=JSON"))
                .andRespond(withServerError());
        assertThatThrownBy(() -> new CelesTrakClient(builder.build()).findByCatalogNumber(25544))
                .isInstanceOf(ExternalServiceException.class);
        server.verify();
    }

    @Test
    void nasaAcceptsPngAndRejectsXmlMasqueradingAsImage() {
        RestClient.Builder okBuilder = RestClient.builder().baseUrl("https://mock.local");
        MockRestServiceServer okServer = MockRestServiceServer.bindTo(okBuilder).build();
        okServer.expect(request -> assertThat(request.getURI().getQuery())
                        .contains("VERSION=1.1.1", "SRS=EPSG:4326", "FORMAT=image/png",
                                "BBOX=-43,-20,-42,-19"))
                .andRespond(withSuccess(new byte[]{(byte) 137, 80, 78, 71}, MediaType.IMAGE_PNG));
        NasaGibsRequest request = new NasaGibsRequest("layer", "-43,-20,-42,-19", 100, 100,
                LocalDate.of(2024, 1, 1), false);
        assertThat(new NasaGibsClient(okBuilder.build()).getImage(request)).hasSize(4);

        RestClient.Builder badBuilder = RestClient.builder().baseUrl("https://mock.local");
        MockRestServiceServer badServer = MockRestServiceServer.bindTo(badBuilder).build();
        badServer.expect(httpRequest -> assertThat(httpRequest.getURI().getPath()).endsWith("/wms.cgi"))
                .andRespond(withSuccess("<?xml version=\"1.0\"?><ServiceException/>", MediaType.IMAGE_PNG));
        assertThatThrownBy(() -> new NasaGibsClient(badBuilder.build()).getImage(request))
                .isInstanceOf(ExternalServiceException.class);
    }
}
