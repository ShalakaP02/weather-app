package com.home.assignment.weatherapp.controller;

import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.service.WeatherService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherController {

    Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherData> getWeatherData(HttpServletRequest httpServletRequest) throws Exception {
        logger.info("WeatherController - getWeatherData request {} ",httpServletRequest);
        WeatherData weatherData = weatherService.getWeatherInfo(httpServletRequest);
        logger.info("WeatherController - getWeatherData response {} ",weatherData);
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }


}
