package com.example.myapplication.managers;

import com.example.myapplication.models.Exam;
import com.example.myapplication.models.Question;

import java.util.List;

public class ExamManager {
    private String examId;
    private String examTitle;
    private String examDate;
    private int examDuration;
    private List<Question> questions;

    public ExamManager(String examId, String examTitle, String examDate, int examDuration, List<Question> questions) {
        this.examId = examId;
        this.examTitle = examTitle;
        this.examDate = examDate;
        this.examDuration = examDuration;
        this.questions = questions;
    }

    public String getExamId() {
        return examId;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public String getExamDate() {
        return examDate;
    }

    public int getExamDuration() {
        return examDuration;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public Exam getExamObject() {
        return new Exam(examId, examTitle, examDate, examDuration, questions);
    }

    public void createNewExam(String examTitle, String examDate, int examDuration, List<Question> questions, OnExamCreatedListener listener) {
        FirestoreManager.getInstance().createNewExam(examTitle, examDate, examDuration, questions, new FirestoreManager.OnQuestionsSavedListener() {
            @Override
            public void onQuestionsSaved() {
                if (listener != null) {
                    listener.onExamCreated();
                }
            }

            @Override
            public void onSaveFailed(Exception e) {
                if (listener != null) {
                    listener.onExamCreationFailed(e);
                }
            }
        });
    }

    public void updateExistingExam(String examId, String examTitle, String examDate, int examDuration, List<Question> questions, OnExamUpdatedListener listener) {
        FirestoreManager.getInstance().updateExistingExam(examId, examTitle, examDate, examDuration, questions, new FirestoreManager.OnQuestionsSavedListener() {
            @Override
            public void onQuestionsSaved() {
                if (listener != null) {
                    listener.onExamUpdated();
                }
            }

            @Override
            public void onSaveFailed(Exception e) {
                if (listener != null) {
                    listener.onExamUpdateFailed(e);
                }
            }
        });
    }


    public interface OnExamCreatedListener {
        void onExamCreated();
        void onExamCreationFailed(Exception e);
    }

    public interface OnExamUpdatedListener {
        void onExamUpdated();
        void onExamUpdateFailed(Exception e);
    }

}
