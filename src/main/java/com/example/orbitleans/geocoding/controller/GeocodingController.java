package com.example.orbitleans.geocoding.controller;


import com.example.orbitleans.geocoding.dto.LocationResponse;
import com.example.orbitleans.geocoding.service.GeocodingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(GeocodingService geocodingService){
        this.geocodingService = geocodingService;
    }
    @GetMapping
    public LocationResponse geocodeAddress(@RequestParam("address") String address){
        return geocodingService.geocode(address);
    }




}
