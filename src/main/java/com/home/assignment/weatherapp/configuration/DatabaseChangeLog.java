package com.home.assignment.weatherapp.configuration;


import com.github.cloudyrock.mongock.ChangeLog;
import com.github.cloudyrock.mongock.ChangeSet;
import com.mongodb.client.MongoDatabase;

@ChangeLog
public class DatabaseChangeLog {

    @ChangeSet(order = "001", id = "createWeatherCollection", author = "")
    public void createWeatherCollection(MongoDatabase database) {
        database.createCollection("weather");
    }

}