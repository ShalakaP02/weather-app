package com.home.assignment.weatherapp.service;

import com.home.assignment.weatherapp.model.GeoLocationData;
import com.home.assignment.weatherapp.model.WeatherData;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;


public interface WeatherService {
     WeatherData getWeatherInformation(HttpServletRequest httpServletRequest);
     List<WeatherData> getWeatherDataByIPAddressFromDB(String ipAddress);
     List<WeatherData> getWeatherDataByLatLonFromDB(double lat, double lon);

     String getIPAddressFromRequestAPICall(HttpServletRequest httpServletRequest);
     Optional<GeoLocationData> getLatLongUsingIpAPICall(String clientIPAddress);
     Optional<WeatherData> getWeatherDataUsingGeolocationAPICall(String clientIPAddress, GeoLocationData locationData);
}
