package com.home.assignment.weatherapp.model;

public class WeatherData {
    private double lat;
    private double lon;
    private String city;
    private String areaName;
    private String description;
    private String tempInCelsius;

    public WeatherData(double lat, double lon, String city, String areaName, String description, String tempInCelsius) {
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.areaName = areaName;
        this.description = description;
        this.tempInCelsius = tempInCelsius;
    }

    public WeatherData() {

    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTempInCelsius() {
        return tempInCelsius;
    }

    public void setTempInCelsius(String tempInCelsius) {
        this.tempInCelsius = tempInCelsius;
    }
}


