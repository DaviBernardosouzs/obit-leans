package com.example.orbitleans.imagery.controller;

import com.example.orbitleans.exception.ApiExceptionHandler;
import com.example.orbitleans.imagery.service.ImageryService;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

class ImageryControllerTest {
    @Test
    void bindsPublicImageryRequestAndReturnsPng() throws Exception {
        ImageryService service = mock(ImageryService.class);
        when(service.getImage(any())).thenReturn(new byte[]{1, 2, 3});
        MockMvc mvc = standaloneSetup(new ImageryController(service))
                .setControllerAdvice(new ApiExceptionHandler()).build();
        mvc.perform(get("/api/imagery").param("address", "Ipatinga").param("layer", "layer")
                        .param("width", "100").param("height", "100").param("time", "2024-01-01")
                        .param("transparent", "false"))
                .andExpect(status().isOk()).andExpect(content().contentType("image/png"))
                .andExpect(content().bytes(new byte[]{1, 2, 3}));
    }
}
