package com.home.assignment.weatherapp.repository;

import com.home.assignment.weatherapp.entity.Weather;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

@DataMongoTest
public class WeatherRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    private Weather weather;

    @Autowired
    WeatherRepository weatherRepository;

    @BeforeEach
    void setUp() {
        weather = new Weather("1","103.208.69.71",18.61,72.71,"Pune","Chinchwad","clear sky","22.29",new Date());
        mongoTemplate.insert(weather);
    }

    @AfterEach
    void cleanUpDatabase() {
        mongoTemplate.getDb().drop();
    }

    @Test
    public void getWeatherDataByIPAddressTest(){
        Optional<List<Weather>> weatherList = weatherRepository.findWeatherByIpAddress("103.208.69.71");
        assertEquals(1, weatherList.get().size());
    }

    @Test
    public void getWeatherDataByLatLongTest(){
        Optional<List<Weather>> weatherList = weatherRepository.findWeatherByLatAndLon(18.61,72.71);
        assertEquals(1, weatherList.get().size());
    }

}
