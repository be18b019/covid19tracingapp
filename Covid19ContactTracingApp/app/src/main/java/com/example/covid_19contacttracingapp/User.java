package com.example.covid_19contacttracingapp;

public class User {

    String username;
    String password;

    //maybe delete
    public User()
    {}

    public User(String username, String password) {
        this.username = username;
        this.password=password;
    }
    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }

}
