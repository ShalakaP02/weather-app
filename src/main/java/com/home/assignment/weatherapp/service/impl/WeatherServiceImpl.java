package com.home.assignment.weatherapp.service.impl;

import com.home.assignment.weatherapp.entity.Weather;
import com.home.assignment.weatherapp.model.GeoLocationData;
import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.model.WeatherDataBuilder;
import com.home.assignment.weatherapp.repository.WeatherRepository;
import com.home.assignment.weatherapp.service.WeatherService;
import com.home.assignment.weatherapp.service.ipservice.IPAddressStrategy;
import com.home.assignment.weatherapp.service.ipservice.impl.IPAddressServiceImplExternally;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class WeatherServiceImpl implements WeatherService {

    Logger logger = LoggerFactory.getLogger(WeatherServiceImpl.class);

    @Autowired
    IPAddressStrategy strategy;

    @Value("${ip.api.url}")
    private String apiUrl;

    @Value("${ip.geo.url}")
    private String geoApiUrl;

    @Value("${ip.weather.url}")
    private String weatherUrl;

    @Value("${ip.weather.api-key}")
    private String weatherApiKey;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WeatherRepository weatherRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override

    public WeatherData getWeatherInfo(HttpServletRequest httpServletRequest) throws Exception {
        logger.debug("WeatherServiceImpl - getWeatherInfo request start {} ",System.currentTimeMillis());
        // getIPAddr
        String clientIPAddress= strategy.executeStrategy(new IPAddressServiceImplExternally(restTemplate,apiUrl));
        if(null == clientIPAddress){
            throw new Exception("Unable to locate ip address");
        }

        //getLatLongUsingIp
        GeoLocationData locationData = getLatLongUsingIp(clientIPAddress);
        if(null == locationData){
            throw new Exception("Unable to locate lat long");
        }


        //check weather by ip nad lat long 5



        //getWeatherUsingLatLong
        WeatherData weatherData = getWeatherDataUsingLatLong(locationData);

        //storeInDb
        Weather weather = modelMapper.map(weatherData,Weather.class);
        weatherRepository.save(weather);

        logger.debug("WeatherServiceImpl - getWeatherInfo request end {} ",System.currentTimeMillis());
        return weatherData;
    }


    private GeoLocationData getLatLongUsingIp(String clientIPAddress){
        String geolocationUrl = String.format(geoApiUrl, clientIPAddress);
        GeoLocationData geo = restTemplate.getForObject(geolocationUrl,GeoLocationData.class);
        return geo;
    }



    private WeatherData getWeatherDataUsingLatLong(GeoLocationData geoLocationData){
        weatherUrl = weatherUrl+"?lat=" + geoLocationData.getLat() + "&lon=" + geoLocationData.getLon()
                + "&appid=" + weatherApiKey;
        String weatherDataString = restTemplate.getForObject(weatherUrl,String.class);

        JSONObject weatherJson = new JSONObject(weatherDataString);
        String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = weatherJson.getJSONObject("main").getDouble("temp");
        String tempInCelsius = String.format("%.2f", temperature - 273.15); // Convert temperature from Kelvin to Celsius
        String areaName = weatherJson.getString("name");

        WeatherData weatherData = new WeatherDataBuilder()
                .setCity(geoLocationData.getCity())
                .setAreaName(areaName)
                .setDescription(description)
                .setTempInCelsius(tempInCelsius)
                .setLat(geoLocationData.getLat())
                .setLon(geoLocationData.getLon())
                .build();

        return weatherData;
    }
}
