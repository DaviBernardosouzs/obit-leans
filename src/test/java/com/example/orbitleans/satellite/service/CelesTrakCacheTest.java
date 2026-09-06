package com.example.orbitleans.satellite.service;

import com.example.orbitleans.orbit.service.OrbitCalculator;
import com.example.orbitleans.satellite.client.celestrak.CelesTrakClient;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

import static com.example.orbitleans.satellite.service.SatelliteServiceTest.iss;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CelesTrakCacheTest {
    private static final CelesTrakClient CLIENT = mock(CelesTrakClient.class);

    @Test
    void repeatedCatalogLookupUsesTwoHourCaffeineCache() {
        when(CLIENT.findByCatalogNumber(25544)).thenReturn(
                new com.example.orbitleans.satellite.client.celestrak.CelesTrakResponse[]{iss()});
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(CacheTestConfig.class)) {
            SatelliteService service = context.getBean(SatelliteService.class);
            assertThat(service.findByCatalogNumber(25544)).isSameAs(service.findByCatalogNumber(25544));
            verify(CLIENT).findByCatalogNumber(25544);
        }
    }

    @Configuration
    @EnableCaching
    static class CacheTestConfig {
        @Bean
        CacheManager cacheManager() {
            CaffeineCacheManager manager = new CaffeineCacheManager("celestrak");
            manager.setCaffeine(Caffeine.newBuilder().maximumSize(500)
                    .expireAfterWrite(Duration.ofHours(2)));
            return manager;
        }

        @Bean
        SatelliteService satelliteService() {
            return new SatelliteService(CLIENT, new OrbitCalculator());
        }
    }
}
