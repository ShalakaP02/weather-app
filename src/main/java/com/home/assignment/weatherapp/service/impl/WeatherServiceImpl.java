package com.home.assignment.weatherapp.service.impl;

import com.home.assignment.weatherapp.entity.Weather;
import com.home.assignment.weatherapp.exception.IPAddressNotFoundException;
import com.home.assignment.weatherapp.model.GeoLocationData;
import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.model.WeatherDataBuilder;
import com.home.assignment.weatherapp.repository.WeatherRepository;
import com.home.assignment.weatherapp.service.WeatherService;
import com.home.assignment.weatherapp.service.ipservice.IPAddressStrategy;
import com.home.assignment.weatherapp.service.ipservice.impl.IPAddressServiceImplExternally;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

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
    @Retry(name = "getWeatherInfo", fallbackMethod = "getWeatherInfoFallback")
    public WeatherData getWeatherInfo(HttpServletRequest httpServletRequest) {
        logger.info("WeatherServiceImpl - getWeatherInfo request start {} ",System.currentTimeMillis());

        /* Fetch IP address using one of the strategies : Using new IPAddressServiceImpl(httpServletRequest) or
           Using new IPAddressServiceImplExternally(restTemplate,apiUrl) */
        Optional<String> clientIPAddress= strategy.executeStrategy(new IPAddressServiceImplExternally(restTemplate,apiUrl));
        if(clientIPAddress.isEmpty())
            throw new IPAddressNotFoundException("Unable to locate client's ip address!");


        /* Fetch lat,lon based on IP address using third party geolocation service */
        Optional<WeatherData> weatherData = Optional.empty();
        Optional<GeoLocationData> locationData = getLatLongUsingIp(clientIPAddress.get());
        if(locationData.isPresent())
            /* Fetch weather data based using third party service */
            weatherData = getWeatherDataUsingGeolocation(locationData.get());

        if(weatherData.isEmpty())
            return null;

        logger.info("WeatherServiceImpl - getWeatherInfo request end {} ",System.currentTimeMillis());
        return weatherData.get();
    }

    public WeatherData getWeatherInfoFallback(Throwable throwable) {
        logger.info("WeatherServiceImpl - getWeatherInfoFallback request");
        return new WeatherDataBuilder().build();
    }

    private Optional<WeatherData> getWeatherDataUsingGeolocation(GeoLocationData locationData){
        WeatherData weatherData = getWeatherDataUsingLatLong(locationData);

        // Store weather data into database
        Weather weather = modelMapper.map(weatherData,Weather.class);
        weatherRepository.save(weather);

        return  Optional.of(weatherData);
    }

    private Optional<GeoLocationData> getLatLongUsingIp(String clientIPAddress){
        String geolocationUrl = String.format(geoApiUrl, clientIPAddress);
        GeoLocationData geo = restTemplate.getForObject(geolocationUrl,GeoLocationData.class);
        geo.setIpAddress(clientIPAddress);
        return Optional.of(geo);
    }



    private WeatherData getWeatherDataUsingLatLong(GeoLocationData geoLocationData){
        String weather_url = weatherUrl+"?lat=" + geoLocationData.getLat() + "&lon=" + geoLocationData.getLon()
                + "&appid=" + weatherApiKey;
        String weatherDataString = restTemplate.getForObject(weather_url,String.class);

        JSONObject weatherJson = new JSONObject(weatherDataString);
        String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = weatherJson.getJSONObject("main").getDouble("temp");
        String tempInCelsius = String.format("%.2f", temperature - 273.15); // Convert temperature from Kelvin to Celsius
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

        return weatherData;
    }


}
