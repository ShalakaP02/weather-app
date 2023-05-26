package com.home.assignment.weatherapp.service;

import com.home.assignment.weatherapp.model.WeatherData;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;


public interface WeatherService {
     WeatherData getWeatherInfo(String ipAddress);
     WeatherData getIPAddressFromRequest(HttpServletRequest httpServletRequest);
     List<WeatherData> getWeatherDataByIPAddress(String ipAddress);
     List<WeatherData> getWeatherDataByLatLon(double lat, double lon);
}
