package com.example.orbitleans.imagery.dto;

import java.time.LocalDate;

public record ImageryRequest(
        String address,
        String layer,
        int width,
        int height,
        LocalDate time,
        boolean transparent
) {
}