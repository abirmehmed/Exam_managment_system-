package com.example.myapplication.models;

import java.util.ArrayList;
import java.util.List;

public class Exam {
    private String examId;
    private String title;
    private String date;
    private int duration;
    private List<Question> questions;

    // No-argument constructor
    public Exam() {
        // This constructor can be left empty
    }

    // Constructor with examId, title, date, and duration
    public Exam(String examId, String title, String date, int duration) {
        this.examId = examId;
        this.title = title;
        this.date = date;
        this.duration = duration;
        this.questions = new ArrayList<>(); // Initialize an empty list of questions
    }

    // Constructor with all fields
    public Exam(String examId, String title, String date, int duration, List<Question> questions) {
        this.examId = examId;
        this.title = title;
        this.date = date;
        this.duration = duration;
        this.questions = questions;
    }
    public Exam(String examTitle, String examDate, int examDuration) {
        this.title = examTitle;
        this.date = examDate;
        this.duration = examDuration;
    }
    // Getter and setter methods
    public String getId() {
        return examId;
    }

    public void setId(String id) {
        this.examId = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public int getDuration() {
        return duration;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }
}