package com.example.myapplication.models;

public class UserData {
    private final String username;
    private final String userType;

    public UserData(String username, String userType) {
        this.username = username;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getUserType() {
        return userType;
    }
}
