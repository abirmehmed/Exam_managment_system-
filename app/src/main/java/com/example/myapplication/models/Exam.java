package com.example.myapplication.models;

public class Exam {
    private String title;
    private String date;
    private int duration;

    // No-argument constructor
    public Exam() {
        // This constructor can be left empty
    }

    public Exam(String title, String date, int duration) {
        this.title = title;
        this.date = date;
        this.duration = duration;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }
}
