package com.example.orbitleans.config;

import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataContext;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrekitConfig {
    public OrekitConfig() {
        DataContext.getDefault().getDataProvidersManager()
                .addProvider(new ClasspathCrawler("orekit-data/UTC-TAI.history"));
    }
}
