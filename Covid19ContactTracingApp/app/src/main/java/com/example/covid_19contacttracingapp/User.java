package com.example.covid_19contacttracingapp;

public class User {

    String Username;
    String Password;
    Double LatitudeLocation;
    Double LongitudeLocation;
    long DateTimeInMillis;
    Integer ID;
    Boolean Covid19Positive;

    public Double getLatitudeLocation() {
        return LatitudeLocation;
    }

    public Double getLongitudeLocation() {
        return LongitudeLocation;
    }

    public long getDateTimeInMillis() {
        return DateTimeInMillis;
    }

    public Boolean getCovid19Positive() {
        return Covid19Positive;
    }

    public User(Integer ID, String username, Double latitudeLocation, Double longitudeLocation, long dateTimeInMillis, Boolean covid19Positive) {
        Username = username;
        LatitudeLocation = latitudeLocation;
        LongitudeLocation = longitudeLocation;
        DateTimeInMillis = dateTimeInMillis;
        this.ID = ID;
        Covid19Positive = covid19Positive;
    }

    public User(String username, String password) {
        this.Username = username;
        this.Password = password;
    }

    public String getUsername() {
        return Username;
    }

    public String getPassword() {
        return Password;
    }

}
