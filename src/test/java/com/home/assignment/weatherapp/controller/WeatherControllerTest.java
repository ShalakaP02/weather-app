package com.home.assignment.weatherapp.controller;

import com.home.assignment.weatherapp.model.WeatherData;
import com.home.assignment.weatherapp.model.WeatherDataBuilder;
import com.home.assignment.weatherapp.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    private WeatherData weatherData;

    private MockHttpServletRequest mockHttpServletRequest;

    @BeforeEach
    void setUp() {
         weatherData = new WeatherDataBuilder()
                .setIpAddress("103.208.69.70")
                .setCity("Pune")
                .setAreaName("Chinchwad")
                .setDescription("clear sky")
                .setTempInCelsius("29.22")
                .setLat(18.61)
                .setLon(73.71)
                .setCreationDate(new Date())
                .build();

    }



    @Test
    public void getWeatherInformationTest() throws Exception {
        when(weatherService.getWeatherInformation(mockHttpServletRequest)).thenReturn(weatherData);
        this.mockMvc.perform(get("/weather")).andDo(print()).andExpect(status().isOk());
    }


    @Test
    public void getWeatherDataByIpTest() throws Exception {
        when(weatherService.getWeatherDataByIPAddressFromDB("103.208.69.70")).thenReturn(List.of(weatherData));
        this.mockMvc.perform(get("/weather/" + "103.208.69.70")).andDo(print()).andExpect(status().isOk());
    }

    @Test
    public void getWeatherDataByLatLonTest() throws Exception {
        when(weatherService.getWeatherDataByLatLonFromDB(18.61,73.71)).thenReturn(List.of(weatherData));
        this.mockMvc.perform(get("/weather/" + "/lat/"+ 18.61 + "/lon/" + 73.71 )).andDo(print()).andExpect(status().isOk());
    }



}
