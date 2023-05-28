package com.home.assignment.weatherapp.service;

import com.home.assignment.weatherapp.entity.Weather;
import com.home.assignment.weatherapp.exception.ResourceNotFoundException;
import com.home.assignment.weatherapp.model.GeoLocationData;
import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.model.WeatherDataBuilder;
import com.home.assignment.weatherapp.repository.WeatherRepository;
import com.home.assignment.weatherapp.service.impl.WeatherServiceImpl;
import com.home.assignment.weatherapp.service.ipservice.IPAddressStrategy;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

//@RunWith(MockitoJUnitRunner.class)
//@ExtendWith(MockitoExtension.class)

public class WeatherServiceTest {


    private WeatherService weatherService;

    @Mock
    private WeatherRepository weatherRepository;

    @Mock
    private IPAddressStrategy strategy;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ModelMapper modelMapper;

    AutoCloseable autoCloseable;

    private List<Weather> weatherList;

    @Mock
    private HttpServletRequest httpServletRequest;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        weatherService = new WeatherServiceImpl(weatherRepository, modelMapper,strategy,restTemplate);
        weatherList = Arrays.asList(new Weather("1","103.208.69.70",18.61,72.71,"Pune","Chinchwad","clear sky","22.29",new Date()));

    }
    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }


    @Test
    public void getWeatherDataByIPAddressFromDBTest(){
        when(weatherRepository.findWeatherByIpAddress("103.208.69.70")).thenReturn(Optional.of(weatherList));

        List<WeatherData> weatherDataList = weatherService.getWeatherDataByIPAddressFromDB("103.208.69.70");
        assertNotNull("Weather data list should not be null", weatherDataList);
        assertThat("There should be one weather data in list", weatherDataList, hasSize(1));

    }

    @Test
    public void getWeatherDataByLatLonFromDBTest() throws Exception{
        when(weatherRepository.findWeatherByIpAddress("103.208.69.70")).thenReturn(Optional.of(weatherList));

        List<WeatherData> weatherDataList = weatherService.getWeatherDataByIPAddressFromDB("103.208.69.70");
        assertNotNull("Weather data list should not be null", weatherDataList);
        assertThat("There should be one weather data in list", weatherDataList, hasSize(1));

    }

    @Test
    public void shouldReturnWeatherDataNotFoundTest() throws Exception{
        when(weatherRepository.findWeatherByIpAddress("103.208.69.70")).thenReturn(Optional.of(weatherList));

        Throwable exception = assertThrows(
                ResourceNotFoundException.class, () -> {
                    weatherService.getWeatherDataByIPAddressFromDB("103.208.69.71");
                }
        );

        String expectedMessage = "Weather data not found!";
        String actualMessage = exception.getMessage();
        Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

    //@Test
    public void getWeatherInformationTest() throws Exception{

        WeatherData  weatherData = new WeatherDataBuilder()
                .setIpAddress("103.208.69.70")
                .setCity("Pune")
                .setAreaName("Chinchwad")
                .setDescription("clear sky")
                .setTempInCelsius("29.22")
                .setLat(18.61)
                .setLon(73.71)
                .setCreationDate(new Date())
                .build();


        GeoLocationData geoLocationData = new GeoLocationData();
        geoLocationData.setCity("Pune");
        geoLocationData.setIpAddress("103.208.69.71");
        geoLocationData.setLat(18.61);
        geoLocationData.setLon(73.71);

        when(weatherService.getIPAddressFromRequestAPICall(httpServletRequest)).thenReturn("103.208.69.71");
        when(weatherService.getLatLongUsingIpAPICall(anyString())).thenReturn(Optional.of(geoLocationData));
        when(weatherService.getWeatherDataUsingGeolocationAPICall(anyString(),geoLocationData)).thenReturn(Optional.of(weatherData));


        WeatherData weatherDataRes = weatherService.getWeatherInformation(httpServletRequest);
        assertNotNull("Weather data list should not be null", weatherDataRes);

    }


}
