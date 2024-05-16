package com.example.myapplication.managers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;
import com.google.android.gms.tasks.Tasks;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.example.myapplication.models.Exam;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

public class FirestoreManager {
    private static final FirestoreManager instance = new FirestoreManager();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirestoreManager getInstance() {
        return instance;
    }

    public void createNewExam(String examTitle, String examDate, int examDuration, List<Question> questions, OnQuestionsSavedListener listener) {
        Exam exam = new Exam(examTitle, examDate, examDuration);
        db.collection("exams")
                .add(exam)
                .addOnSuccessListener(examRef -> {
                    String examId = examRef.getId();
                    saveQuestionsToFirestore(examId, questions, listener);
                })
                .addOnFailureListener(e -> {
                    listener.onSaveFailed(e);
                });
    }

    public void updateExistingExam(String examId, String examTitle, String examDate, int examDuration, List<Question> questions, OnQuestionsSavedListener listener) {
        Exam exam = new Exam(examId, examTitle, examDate, examDuration);
        db.collection("exams")
                .document(examId)
                .set(exam)
                .continueWithTask(task -> {
                    return db.collection("exams").document(examId).collection("questions").get()
                            .continueWithTask(questionTask -> {
                                WriteBatch batch = db.batch();
                                for (DocumentSnapshot document : questionTask.getResult().getDocuments()) {
                                    batch.delete(document.getReference());
                                }
                                return batch.commit();
                            });
                })
                .addOnSuccessListener(aVoid -> {
                    saveQuestionsToFirestore(examId, questions, listener);
                })
                .addOnFailureListener(e -> {
                    listener.onSaveFailed(e);
                });
    }
    private Task<List<Question>> retrieveExistingQuestions(String examId) {
        return db.collection("exams")
                .document(examId)
                .collection("questions")
                .get()
                .continueWith(task -> {
                    List<Question> existingQuestions = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Question question = document.toObject(Question.class);
                        question.setDocumentId(document.getId());
                        existingQuestions.add(question);
                    }
                    return existingQuestions;
                });
    }


    private void saveQuestionsToFirestore(String examId, List<Question> newQuestions, OnQuestionsSavedListener listener) {
        db.runTransaction(transaction -> {
            WriteBatch batch = db.batch();
            for (Question question : newQuestions) {
                DocumentReference questionRef = db.collection("exams").document(examId).collection("questions").document();
                question.setDocumentId(questionRef.getId());
                batch.set(questionRef, question);
            }
            return batch.commit();
        }).addOnSuccessListener(aVoid -> {
            listener.onQuestionsSaved();
        }).addOnFailureListener(e -> {
            listener.onSaveFailed(e);
        });
    }
    private List<Question> filterOutDeletedQuestions(List<Question> questions) {
        List<Question> updatedQuestions = new ArrayList<>();
        for (Question question : questions) {
            if (question.getDocumentId() != null) {
                updatedQuestions.add(question);
            }
        }
        return updatedQuestions;
    }

    private Set<String> getExistingQuestionIds(List<Question> existingQuestions) {
        Set<String> allQuestionIds = new HashSet<>();
        for (Question existingQuestion : existingQuestions) {
            allQuestionIds.add(existingQuestion.getDocumentId());
        }
        return allQuestionIds;
    }

    private void deleteRemovedQuestions(WriteBatch batch, String examId, List<Question> existingQuestions, Set<String> allQuestionIds) {
        for (Question removedQuestion : existingQuestions) {
            if (!allQuestionIds.contains(removedQuestion.getDocumentId())) {
                batch.delete(db.collection("exams").document(examId).collection("questions").document(removedQuestion.getDocumentId()));
            }
        }
    }

    private void saveNewQuestions(WriteBatch batch, String examId, List<Question> newQuestions, Set<String> allQuestionIds) {
        for (Question question : newQuestions) {
            if (!allQuestionIds.contains(question.getDocumentId())) {
                DocumentReference questionRef = db.collection("exams").document(examId).collection("questions").document();
                batch.set(questionRef, question.toMap());
                question.setDocumentId(questionRef.getId());
                allQuestionIds.add(questionRef.getId());
            }
        }
    }

    private void updateExistingQuestions(WriteBatch batch, String examId, List<Question> existingQuestions, Set<String> allQuestionIds) {
        for (Question existingQuestion : existingQuestions) {
            if (allQuestionIds.contains(existingQuestion.getDocumentId())) {
                DocumentReference questionRef = db.collection("exams").document(examId).collection("questions").document(existingQuestion.getDocumentId());
                batch.set(questionRef, existingQuestion.toMap());
            }
        }
    }



    public void retrieveExamQuestions(String examId, OnQuestionsRetrievedListener listener) {
        db.collection("exams")
                .document(examId)
                .collection("questions")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Question> questions = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            String text = document.getString("text");
                            QuestionType type = QuestionType.valueOf(document.getString("type"));
                            List<String> options = (List<String>) document.get("options");
                            String answer = document.getString("answer");
                            Question question = new Question(text, type, options, answer);
                            question.setDocumentId(document.getId()); // Set the document ID
                            questions.add(question);
                        }
                        listener.onQuestionsRetrieved(questions);
                    } else {
                        listener.onQuestionsRetrievalFailed(task.getException());
                    }
                });
    }


    public void deleteQuestionFromFirestore(String examId, String questionDocumentId) {
        if (questionDocumentId != null) {
            db.collection("exams")
                    .document(examId)
                    .collection("questions")
                    .document(questionDocumentId)
                    .delete();
        }
    }

    public interface OnQuestionsSavedListener {
        void onQuestionsSaved();
        void onSaveFailed(Exception e);
    }
    public interface OnQuestionsRetrievedListener {
        void onQuestionsRetrieved(List<Question> questions);
        void onQuestionsRetrievalFailed(Exception e);
    }
    public FirebaseFirestore getDb() {
        return db;
    }

}
