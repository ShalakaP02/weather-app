package com.home.assignment.weatherapp;

import com.home.assignment.weatherapp.entity.Weather;
import com.home.assignment.weatherapp.model.GeoLocationData;
import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.model.WeatherDataBuilder;
import com.home.assignment.weatherapp.repository.WeatherRepository;
import com.home.assignment.weatherapp.service.impl.WeatherServiceImpl;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Component
public class WeatherUtils {

    Logger logger = LoggerFactory.getLogger(WeatherUtils.class);

    public static final Double KELVIN_TO_CELSIUS_VAL = 273.15;
    public static final String STRING_FORMAT = "%.2f";

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherRepository weatherRepository;

    public Optional<GeoLocationData> getLatLongUsingIp(String clientIPAddress,String geoApiUrl){
        logger.info("WeatherServiceImpl - getLatLongUsingIp request start {} ",System.currentTimeMillis());
        String geolocationUrl = String.format(geoApiUrl, clientIPAddress);
        GeoLocationData geo = restTemplate.getForObject(geolocationUrl,GeoLocationData.class);
        if(null != geo)
            geo.setIpAddress(clientIPAddress);
        logger.info("WeatherServiceImpl - getLatLongUsingIp request end {} ",System.currentTimeMillis());
        return Optional.ofNullable(geo);
    }


    public Optional<WeatherData> getWeatherDataUsingGeolocation(GeoLocationData locationData,String weatherUrl, String weatherApiKey){

        // Fetch weather data from 3rd party api using lat,lon
        Optional<WeatherData> weatherData = getWeatherDataUsingLatLong(locationData,weatherUrl,weatherApiKey);

        // Store weather data into database
        if(weatherData.isPresent()){
            Weather weather = modelMapper.map(weatherData,Weather.class);
            weatherRepository.save(weather);
        }

        return  weatherData;
    }


    public Optional<WeatherData> getWeatherDataUsingLatLong(GeoLocationData geoLocationData,String weatherUrl, String weatherApiKey ){
        logger.info("WeatherServiceImpl - getWeatherDataUsingLatLong request start {} ",System.currentTimeMillis());
        String weather_url = weatherUrl+"?lat=" + geoLocationData.getLat() + "&lon=" + geoLocationData.getLon()
                + "&appid=" + weatherApiKey;

        String weatherDataString = restTemplate.getForObject(weather_url,String.class);

        JSONObject weatherJson = new JSONObject(weatherDataString);
        String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = weatherJson.getJSONObject("main").getDouble("temp");
        String tempInCelsius = String.format(STRING_FORMAT, temperature - KELVIN_TO_CELSIUS_VAL); // Convert temperature from Kelvin to Celsius
        String areaName = weatherJson.getString("name");

        WeatherData weatherData = new WeatherDataBuilder()
                .setIpAddress(geoLocationData.getIpAddress())
                .setCity(geoLocationData.getCity())
                .setAreaName(areaName)
                .setDescription(description)
                .setTempInCelsius(tempInCelsius)
                .setLat(geoLocationData.getLat())
                .setLon(geoLocationData.getLon())
                .build();

        logger.info("WeatherServiceImpl - getWeatherDataUsingLatLong request end {} ",System.currentTimeMillis());
        return Optional.of(weatherData);
    }

}
