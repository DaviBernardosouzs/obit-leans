package com.example.orbitleans.satellite.controller;

import com.example.orbitleans.satellite.domain.Satellite;
import com.example.orbitleans.satellite.dto.SatelliteResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
@RequestMapping("/orbit-leans")
public class SatelliteController {


    @GetMapping("/seach-satellite")
    public Satellite requestsatellite(){

        return requestsatellite();
    }

    @PostMapping("/results-satellite")
    public SatelliteResponse satelliteResponse(){

        return satelliteResponse();
    }








}
