package com.home.assignment.weatherapp.model;

import java.util.Date;

public class WeatherDataBuilder {
    private String ipAddress;
    private double lat;
    private double lon;
    private String city;
    private String areaName;
    private String description;
    private String tempInCelsius;
    private Date creationDate;

    public WeatherDataBuilder setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
        return this;
    }

    public WeatherDataBuilder setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public WeatherDataBuilder setLon(double lon) {
        this.lon = lon;
        return this;
    }

    public WeatherDataBuilder setCity(String city) {
        this.city = city;
        return this;
    }

    public WeatherDataBuilder setAreaName(String areaName) {
        this.areaName = areaName;
        return this;
    }

    public WeatherDataBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public WeatherDataBuilder setTempInCelsius(String tempInCelsius) {
        this.tempInCelsius = tempInCelsius;
        return this;
    }

    public WeatherDataBuilder setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public WeatherData build() {
        return new WeatherData(ipAddress, lat, lon, city, areaName, description, tempInCelsius,creationDate);
    }
}