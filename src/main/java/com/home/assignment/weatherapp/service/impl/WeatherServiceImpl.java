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
import com.home.assignment.weatherapp.utils.WeatherUtils;
import io.github.resilience4j.retry.annotation.Retry;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.xml.transform.sax.SAXResult;
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
    private WeatherUtils weatherUtils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WeatherRepository weatherRepository;

    @Override
    @Retry(name = "getWeatherInfo", fallbackMethod = "getWeatherInfoFallback")
    public WeatherData getIPAddressFromRequest(HttpServletRequest httpServletRequest) {
        logger.info("WeatherServiceImpl - getIPAddressFromRequest request start {} ",System.currentTimeMillis());

        // Fetch IP Address
        Optional<String> clientIPAddress= strategy.executeStrategy(new IPAddressServiceImplExternally(restTemplate,apiUrl));
        if(clientIPAddress.isEmpty())
            throw new IPAddressNotFoundException("Unable to locate client's ip address!");

        // Fetch weather data
        WeatherData weatherData = getWeatherInfo(clientIPAddress.get());

        logger.info("WeatherServiceImpl - getIPAddressFromRequest request end {} ",System.currentTimeMillis());
        return weatherData;
    }



    @Override
    public WeatherData getWeatherInfo(String clientIPAddress) {
        logger.info("WeatherServiceImpl - getWeatherInfo request start {} ",System.currentTimeMillis());

        // Fetch lat,lon based on IP address using third party geolocation service
        Optional<WeatherData> weatherData = Optional.empty();
        Optional<GeoLocationData> locationData = weatherUtils.getLatLongUsingIp(clientIPAddress,geoApiUrl);

        // Fetch weather data
        if(locationData.isPresent())
            weatherData = weatherUtils.getWeatherDataUsingGeolocation(locationData.get(), weatherUrl, weatherApiKey);

        if(weatherData.isEmpty())
            return null;

        logger.info("WeatherServiceImpl - getWeatherInfo request end {} ",System.currentTimeMillis());
        return weatherData.get();
    }


    /* FallBack method for getWeatherInfo */
    public WeatherData getWeatherInfoFallback(Throwable throwable) {
        logger.info("WeatherServiceImpl - getWeatherInfoFallback");
        return new WeatherDataBuilder().build();
    }


    @Override
    @Cacheable(cacheNames = "weatherByIPCache", key = "#ipAddress")
    public List<WeatherData> getWeatherDataByIPAddress(String ipAddress){
        logger.info("WeatherServiceImpl - getWeatherDataByIPAddress - ipaddress {}",ipAddress);
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
    public List<WeatherData> getWeatherDataByLatLon(double lat, double lon){
        logger.info("WeatherServiceImpl - getWeatherDataByLatLon - lat lon {} {}",lat,lon);
        Optional<List<Weather>> weatherList = weatherRepository.findWeatherByLatAndLon(lat,lon);
        if(weatherList.isPresent() && !weatherList.get().isEmpty()) {
            return weatherList.get().stream().map(weather -> modelMapper.map(weather, WeatherData.class))
                    .collect(Collectors.toList());
        }else {
            throw new ResourceNotFoundException("Weather data not found!");
        }
    }

}
