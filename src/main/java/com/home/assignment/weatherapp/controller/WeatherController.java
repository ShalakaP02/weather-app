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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WeatherController {

    Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    WeatherService weatherService;

    @GetMapping("/weather")
    public ResponseEntity<WeatherData> getWeatherData(HttpServletRequest httpServletRequest) {
        logger.info("WeatherController - getWeatherData request {} ",httpServletRequest);
        WeatherData weatherData = weatherService.getIPAddressFromRequest(httpServletRequest);
        logger.info("WeatherController - getWeatherData response {} ",weatherData);
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }

    @GetMapping("/weather/{ip}")
    public ResponseEntity<List<WeatherData>> getWeatherData(@PathVariable String ip) {
        logger.info("WeatherController - getWeatherData request {} ",ip);
        List<WeatherData> weatherData = weatherService.getWeatherDataByIPAddress(ip);
        logger.info("WeatherController - getWeatherData response {} ",weatherData);
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }

    @GetMapping("/weather/lat/{lat}/lon/{lon}")
    public ResponseEntity< List<WeatherData>> getWeatherData(@PathVariable double lat, @PathVariable double lon) {
        logger.info("WeatherController - getWeatherData request {} {}",lat,lon);
        List<WeatherData> weatherData = weatherService.getWeatherDataByLatLon(lat,lon);
        logger.info("WeatherController - getWeatherData response {} ",weatherData);
        return new ResponseEntity<>(weatherData, HttpStatus.OK);
    }


}
