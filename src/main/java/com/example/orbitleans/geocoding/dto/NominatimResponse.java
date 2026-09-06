package com.example.orbitleans.geocoding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NominatimResponse(
        String lat,
        String lon,
        @JsonProperty("display_name")
        String displayname
) {
}





