package com.home.assignment.weatherapp.service.impl;

import com.home.assignment.weatherapp.entity.Weather;
import com.home.assignment.weatherapp.exception.IPAddressNotFoundException;
import com.home.assignment.weatherapp.exception.ResourceNotFoundException;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private ModelMapper modelMapper;

    @Autowired
    private WeatherRepository weatherRepository;

    public static final Double KELVIN_TO_CELSIUS_VAL = 273.15;
    public static final String STRING_FORMAT = "%.2f";



    @Override
    @Retry(name = "getWeatherInfo", fallbackMethod = "getWeatherInfoFallback")
    public WeatherData getWeatherInformation(HttpServletRequest httpServletRequest) {
        logger.info("WeatherServiceImpl - getWeatherInformation request start {} ",System.currentTimeMillis());

        // Fetch IP Address
        String clientIPAddress = getIPAddressFromRequestAPICall(httpServletRequest);

        // Fetch Lat and Lon geolocation data
        Optional<GeoLocationData> locationData = getLatLongUsingIpAPICall(clientIPAddress);

        // Fetch weather data
        Optional<WeatherData> weatherData = Optional.empty();
        if(locationData.isPresent())
            weatherData = getWeatherDataUsingGeolocationAPICall(clientIPAddress,locationData.get());

        if(weatherData.isEmpty())
            return null;

        logger.info("WeatherServiceImpl - getWeatherInformation request end {} ",System.currentTimeMillis());
        return weatherData.get();
    }


    public String getIPAddressFromRequestAPICall(HttpServletRequest httpServletRequest) {
        logger.info("WeatherServiceImpl - getIPAddressFromRequest request start {} ",System.currentTimeMillis());

        // Fetch IP Address based on strategy
        Optional<String> clientIPAddress= strategy.executeStrategy(new IPAddressServiceImplExternally(restTemplate,apiUrl));
        if(clientIPAddress.isEmpty())
            throw new IPAddressNotFoundException("Unable to locate client's ip address!");

        logger.info("WeatherServiceImpl - getIPAddressFromRequest request end {} ",System.currentTimeMillis());
        return clientIPAddress.get();
    }

    public Optional<GeoLocationData> getLatLongUsingIpAPICall(String clientIPAddress){
        logger.info("WeatherServiceImpl - getLatLongUsingIp request start {} ",System.currentTimeMillis());
        // Fetch lat,lon based on IP address using third party geolocation service
        String geolocationUrl = String.format(geoApiUrl, clientIPAddress);
        GeoLocationData geo = restTemplate.getForObject(geolocationUrl,GeoLocationData.class);
        if(null != geo)
            geo.setIpAddress(clientIPAddress);
        logger.info("WeatherServiceImpl - getLatLongUsingIp request end {} ",System.currentTimeMillis());
        return Optional.ofNullable(geo);
    }


    @Cacheable(cacheNames = "weatherCache", key = "#clientIPAddress")
    public Optional<WeatherData> getWeatherDataUsingGeolocationAPICall(String clientIPAddress, GeoLocationData locationData){
        logger.info("********* WeatherServiceImpl -getWeatherDataUsingGeolocation start *************");
        // Fetch weather data from 3rd party api using lat,lon
        String weather_url = weatherUrl+"?lat=" + locationData.getLat() + "&lon=" + locationData.getLon()
                + "&appid=" + weatherApiKey;

        String weatherDataString = restTemplate.getForObject(weather_url,String.class);

        JSONObject weatherJson = new JSONObject(weatherDataString);
        String description = weatherJson.getJSONArray("weather").getJSONObject(0).getString("description");
        double temperature = weatherJson.getJSONObject("main").getDouble("temp");
        String tempInCelsius = String.format(STRING_FORMAT, temperature - KELVIN_TO_CELSIUS_VAL); // Convert temperature from Kelvin to Celsius
        String areaName = weatherJson.getString("name");

        WeatherData weatherData = new WeatherDataBuilder()
                .setIpAddress(clientIPAddress)
                .setCity(locationData.getCity())
                .setAreaName(areaName)
                .setDescription(description)
                .setTempInCelsius(tempInCelsius)
                .setLat(locationData.getLat())
                .setLon(locationData.getLon())
                .setCreationDate(new Date())
                .build();


        // Store weather data into database
        Weather weather = modelMapper.map(weatherData,Weather.class);
        weatherRepository.save(weather);

        logger.info("********** WeatherServiceImpl - getWeatherDataUsingGeolocation request end ************ ");
        return Optional.of(weatherData);
    }


    /* FallBack method for getWeatherInfo */
    public WeatherData getWeatherInfoFallback(Throwable throwable) {
        logger.info("WeatherServiceImpl - getWeatherInfoFallback");
        return new WeatherDataBuilder().build();
    }


    @Override
    @Cacheable(cacheNames = "weatherByIPCache", key = "#ipAddress")
    public List<WeatherData> getWeatherDataByIPAddressFromDB(String ipAddress){
        logger.info("****** WeatherServiceImpl - getWeatherDataByIPAddress - ipaddress {}",ipAddress);
        Optional<List<Weather>> weatherList = weatherRepository.findWeatherByIpAddress(ipAddress);
        if(weatherList.isPresent() && !weatherList.get().isEmpty()) {
            return weatherList.get().stream().map(weather -> modelMapper.map(weather, WeatherData.class))
                    .collect(Collectors.toList());
        }else {
            throw new ResourceNotFoundException("Weather data not found!");
        }
    }

    @Override
    @Cacheable(cacheNames = "weatherByLatLonCache", key = "{#lat, #lon}")
    public List<WeatherData> getWeatherDataByLatLonFromDB(double lat, double lon){
        logger.info("******** WeatherServiceImpl - getWeatherDataByLatLon - lat lon {} {}",lat,lon);
        Optional<List<Weather>> weatherList = weatherRepository.findWeatherByLatAndLon(lat,lon);
        if(weatherList.isPresent() && !weatherList.get().isEmpty()) {
            return weatherList.get().stream().map(weather -> modelMapper.map(weather, WeatherData.class))
                    .collect(Collectors.toList());
        }else {
            throw new ResourceNotFoundException("Weather data not found!");
        }
    }

}
