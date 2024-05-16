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

import com.example.myapplication.models.Exam;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

public class FirestoreManager {
    private static final FirestoreManager instance = new FirestoreManager();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static FirestoreManager getInstance() {
        return instance;
    }

    public Task<Void> createNewExam(String examTitle, String examDate, int examDuration, List<Question> questions) {
        Exam exam = new Exam(examTitle, examDate, examDuration);
        exam.setQuestions(questions);
        return db.collection("exams")
                .add(exam)
                .continueWithTask(task -> {
                    DocumentReference examRef = task.getResult();
                    String examId = examRef.getId();
                    return saveQuestionsToFirestore(examId, questions, new ArrayList<>());
                });
    }

    public Task<Void> updateExistingExam(String examId, String examTitle, String examDate, int examDuration, List<Question> questions) {
        Exam exam = new Exam(examId, examTitle, examDate, examDuration, questions);
        return db.collection("exams")
                .document(examId)
                .set(exam)
                .continueWithTask(task -> retrieveExistingQuestions(examId))
                .continueWithTask(task -> saveQuestionsToFirestore(examId, questions, task.getResult()));
    }


    private Task<List<Question>> retrieveExistingQuestions(String examId) {
        return db.collection("exams")
                .document(examId)
                .collection("questions")
                .get()
                .continueWith(task -> {
                    List<Question> existingQuestions = new ArrayList<>();
                    for (DocumentSnapshot document : task.getResult().getDocuments()) {
                        Map<String, Object> questionData = document.getData();
                        String text = (String) questionData.get("text");
                        QuestionType type = QuestionType.valueOf((String) questionData.get("type"));
                        List<String> options = (List<String>) questionData.get("options");
                        String answer = (String) questionData.get("answer");
                        Question question = new Question(text, type, options, answer);
                        question.setDocumentId(document.getId()); // Set the document ID
                        existingQuestions.add(question);
                    }
                    return existingQuestions;
                });
    }


    private Task<Void> saveQuestionsToFirestore(String examId, List<Question> newQuestions, List<Question> existingQuestions) {
        return db.runTransaction(transaction -> {
            WriteBatch batch = db.batch();
            Set<String> allQuestionIds = new HashSet<>();

            // Remove deleted questions from the newQuestions list
            List<Question> updatedNewQuestions = new ArrayList<>();
            for (Question question : newQuestions) {
                if (question.getDocumentId() != null) {
                    updatedNewQuestions.add(question);
                }
            }

            List<Question> updatedExistingQuestions = filterOutDeletedQuestions(existingQuestions);

            allQuestionIds.addAll(getExistingQuestionIds(updatedExistingQuestions));

            deleteRemovedQuestions(batch, examId, updatedExistingQuestions, allQuestionIds);

            saveNewQuestions(batch, examId, updatedNewQuestions, allQuestionIds);

            updateExistingQuestions(batch, examId, updatedExistingQuestions, allQuestionIds);

            return batch.commit();
        }).continueWithTask(task -> {
            if (task.isSuccessful()) {
                return task.getResult();
            } else {
                throw task.getException();
            }
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


    public interface OnQuestionsRetrievedListener {
        void onQuestionsRetrieved(List<Question> questions);
        void onQuestionsRetrievalFailed(Exception e);
    }
    public FirebaseFirestore getDb() {
        return db;
    }

}
