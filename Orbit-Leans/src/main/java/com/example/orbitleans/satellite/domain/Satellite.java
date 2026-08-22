package com.example.orbitleans.satellite.domain;
import com.example.orbitleans.orbit.OrbitElements;

public record Satellite(

   String catalognumber,
   String name,
   //SatelliteType type,
   OrbitElements orbitElements

) {}