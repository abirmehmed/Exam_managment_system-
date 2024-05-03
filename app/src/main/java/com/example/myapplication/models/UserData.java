package com.example.myapplication.models;

public class UserData {
    private String username;
    private String dateOfBirth;
    private String userType;

    public UserData(String username, String dateOfBirth, String userType) {
        this.username = username;
        this.dateOfBirth = dateOfBirth;
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getUserType() {
        return userType;
    }
}
