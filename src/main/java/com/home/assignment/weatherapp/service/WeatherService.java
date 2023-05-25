package com.home.assignment.weatherapp.service;

import com.home.assignment.weatherapp.model.WeatherData;
import jakarta.servlet.http.HttpServletRequest;


public interface WeatherService {
     WeatherData getWeatherInfo(HttpServletRequest httpServletRequest);
}
