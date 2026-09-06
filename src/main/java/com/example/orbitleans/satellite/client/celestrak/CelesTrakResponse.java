package com.example.orbitleans.satellite.client.celestrak;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CelesTrakResponse(

        @JsonProperty("OBJECT_NAME")
        String objectName,

        @JsonProperty("OBJECT_ID")
        String objectId,

        @JsonProperty("NORAD_CAT_ID")
        int noradCatalogId,

        @JsonProperty("EPOCH")
        String epoch,

        @JsonProperty("INCLINATION")
        double inclination,

        @JsonProperty("ECCENTRICITY")
        double eccentricity,

        @JsonProperty("MEAN_MOTION")
        double meanMotion,

        @JsonProperty("RA_OF_ASC_NODE")
        double rightAscensionAscendingNode,

        @JsonProperty("ARG_OF_PERICENTER")
        double argumentOfPericenter,

        @JsonProperty("MEAN_ANOMALY")
        double meanAnomaly,

        @JsonProperty("MEAN_MOTION_DOT")
        double meanMotionDot,

        @JsonProperty("MEAN_MOTION_DDOT")
        double meanMotionDoubleDot,
        @JsonProperty("EPHEMERIS_TYPE")
        int ephemerisType,

        @JsonProperty("ELEMENT_SET_NO")
        int elementSetNumber,

        @JsonProperty("REV_AT_EPOCH")
        int revolutionAtEpoch,

        @JsonProperty("BSTAR")
        double bStar


) {
}
