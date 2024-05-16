package com.example.myapplication.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class Question {
        private String text;
        private QuestionType type;
        private List<String> options;
        private String answer;
        private String documentId;

        // No-argument constructor required by Firestore
        public Question() {
            // This constructor is required by Firestore
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

        public String getDocumentId() {
            return documentId;
        }

        public void setDocumentId(String documentId) {
            this.documentId = documentId;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> data = new HashMap<>();
            data.put("text", getText());
            data.put("type", getType().toString());
            data.put("options", getOptions());
            data.put("answer", getAnswer());
            return data;
        }
    }
