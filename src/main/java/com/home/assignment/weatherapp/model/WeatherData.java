package com.home.assignment.weatherapp.model;

import java.util.Date;

public class WeatherData {

    private String ipAddress;
    private double lat;
    private double lon;
    private String city;
    private String areaName;
    private String description;
    private String tempInCelsius;
    private Date creationDate;

    public WeatherData(){
    }

    public WeatherData(String ipAddress, double lat, double lon, String city, String areaName, String description, String tempInCelsius, Date creationDate) {
        this.ipAddress = ipAddress;
        this.lat = lat;
        this.lon = lon;
        this.city = city;
        this.areaName = areaName;
        this.description = description;
        this.tempInCelsius = tempInCelsius;
        this.creationDate = creationDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}


