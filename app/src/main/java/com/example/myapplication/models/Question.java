package com.example.myapplication.models;

import java.util.List;

public class Question {
    private String text;
    private QuestionType type;
    private List<String> options;
    private String answer; // For short answer questions

    public Question() {
        // Default constructor required for Firestore deserialization
    }

    public Question(String text, QuestionType type, List<String> options, String answer) {
        this.text = text;
        this.type = type;
        this.options = options;
        this.answer = answer;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
