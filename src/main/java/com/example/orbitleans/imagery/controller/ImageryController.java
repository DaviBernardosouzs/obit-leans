package com.example.orbitleans.imagery.controller;


import com.example.orbitleans.imagery.dto.ImageryRequest;
import com.example.orbitleans.imagery.service.ImageryService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/imagery")
public class ImageryController {


    private final ImageryService imageryService;

    public ImageryController(ImageryService imageryService) {
        this.imageryService = imageryService;
    }

    @GetMapping(produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getImage(@ModelAttribute ImageryRequest request) {

        byte[] image = imageryService.getImage(request);

        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(image);
    }


}
