package com.example.myapplication.managers;

import com.example.myapplication.models.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionManager {
    private List<Question> questions;

    public QuestionManager(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = new ArrayList<>(questions);
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public void updateQuestion(int index, Question updatedQuestion) {
        if (index >= 0 && index < questions.size()) {
            questions.set(index, updatedQuestion);
        }
    }

    public void deleteQuestion(int index) {
        if (index >= 0 && index < questions.size()) {
            questions.remove(index);
        }
    }

    public void rearrangeQuestions(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(questions, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(questions, i, i - 1);
            }
        }
    }

    public void updateQuestionOrderInFirestore(String examId, String examTitle, String examDate, int examDuration, OnQuestionOrderUpdatedListener listener) {
        FirestoreManager.getInstance().updateExistingExam(examId, examTitle, examDate, examDuration, questions)
                .addOnSuccessListener(aVoid -> listener.onQuestionOrderUpdated())
                .addOnFailureListener(e -> listener.onQuestionOrderUpdateFailed(e));
    }

    public interface OnQuestionOrderUpdatedListener {
        void onQuestionOrderUpdated();
        void onQuestionOrderUpdateFailed(Exception e);
    }
}
