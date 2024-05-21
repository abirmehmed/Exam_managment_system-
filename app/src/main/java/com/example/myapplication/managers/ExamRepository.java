package com.example.myapplication.managers;

import com.example.myapplication.models.Exam;
import com.example.myapplication.models.Question;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ExamRepository {
    private static final String EXAMS_COLLECTION = "exams";
    private static final String QUESTIONS_COLLECTION = "questions";

    private final FirebaseFirestore firestore;

    public ExamRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getExamData(String examId, OnExamDataFetchedListener listener) {
        firestore.collection(EXAMS_COLLECTION)
                .document(examId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Exam exam = documentSnapshot.toObject(Exam.class);
                        if (exam != null) {
                            exam.setId(documentSnapshot.getId());

                            firestore.collection(EXAMS_COLLECTION)
                                    .document(examId)
                                    .collection(QUESTIONS_COLLECTION)
                                    .get()
                                    .addOnSuccessListener(querySnapshot -> {
                                        List<Question> questions = new ArrayList<>();
                                        for (QueryDocumentSnapshot document : querySnapshot) {
                                            Question question = document.toObject(Question.class);
                                            question.setDocumentId(document.getId());
                                            questions.add(question);
                                        }
                                        exam.setQuestions(questions);
                                        listener.onExamDataFetched(exam);
                                    })
                                    .addOnFailureListener(listener::onExamDataFetchFailed);
                        } else {
                            listener.onExamDataFetchFailed(new Exception("Exam not found"));
                        }
                    } else {
                        listener.onExamDataFetchFailed(new Exception("Exam not found"));
                    }
                })
                .addOnFailureListener(listener::onExamDataFetchFailed);
    }

    public interface OnExamDataFetchedListener {
        void onExamDataFetched(Exam exam);
        void onExamDataFetchFailed(Exception e);
    }
}



