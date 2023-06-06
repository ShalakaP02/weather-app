package com.home.assignment.weatherapp.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Value("${ip.api.url}")
    private String apiUrl;

    @Value("${ip.geo.url}")
    private String geoApiUrl;

    @Value("${ip.weather.url}")
    private String weatherUrl;

    @Value("${ip.weather.api-key}")
    private String weatherApiKey;

    public String getApiUrl() {
        return apiUrl;
    }

    public String getGeoApiUrl() {
        return geoApiUrl;
    }

    public String getWeatherUrl() {
        return weatherUrl;
    }

    public String getWeatherApiKey() {
        return weatherApiKey;
    }
}
