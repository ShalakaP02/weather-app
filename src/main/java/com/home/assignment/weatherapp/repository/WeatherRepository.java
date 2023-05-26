package com.home.assignment.weatherapp.repository;

import com.home.assignment.weatherapp.entity.Weather;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {
    Optional<List<Weather>> findWeatherByIpAddress(String ipAddress);
    Optional<List<Weather>> findWeatherByLatAndLon(double lat,double lon);
}
