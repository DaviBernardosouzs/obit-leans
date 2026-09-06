package com.example.orbitleans.imagery.client.nasa.dto;

import java.time.LocalDate;


public record NasaGibsRequest(
        String layer,
        String bbox,
        int width,
        int height,
        LocalDate time,
        boolean transparent
) {
}
