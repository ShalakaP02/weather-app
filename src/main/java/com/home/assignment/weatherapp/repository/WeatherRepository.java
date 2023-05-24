package com.home.assignment.weatherapp.repository;

import com.home.assignment.weatherapp.entity.Weather;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends MongoRepository<Weather, String> {
}
