package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.example.myapplication.models.Exam;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.QuestionAdapter;
import com.example.myapplication.models.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.example.myapplication.models.QuestionType;
import androidx.annotation.Nullable;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentSnapshot;

import android.widget.Button;
import com.example.myapplication.models.Exam;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Log;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.DocumentReference;
import java.lang.Exception;


import java.util.Collections;
import java.util.HashMap;


public class ExamEditorActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionClickListener, QuestionAdapter.OnQuestionEditListener, QuestionAdapter.OnQuestionDeleteListener {

        private static final Gson GSON = new Gson();
        private FloatingActionButton fabAddQuestion;
        private static final int REQUEST_CODE_ADD_QUESTION = 1;
        private static final int REQUEST_CODE_EDIT_QUESTION = 2;
        private RecyclerView questionRecyclerView;
        private QuestionAdapter questionAdapter;
        private List<Question> questions = new ArrayList<>();
        private ItemTouchHelper itemTouchHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_editor);

        // Retrieve the exam data from the Intent extras
        String examTitle = getIntent().getStringExtra("examTitle");
        String examDate = getIntent().getStringExtra("examDate");
        int examDuration = getIntent().getIntExtra("examDuration", 0);
        String examId = getIntent().getStringExtra("examId");

        // Set the exam title in the toolbar
        TextView tvExamTitle = findViewById(R.id.tv_exam_title);
        tvExamTitle.setText(examTitle);

        // Initialize the RecyclerView
        questionRecyclerView = findViewById(R.id.rv_questions);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the questionAdapter
        questionAdapter = new QuestionAdapter(new ArrayList<>());
        questionRecyclerView.setAdapter(questionAdapter);

        // Set up the question adapter listeners
        questionAdapter.setOnQuestionClickListener(this);
        questionAdapter.setOnQuestionEditListener(this);
        questionAdapter.setOnQuestionDeleteListener(this);

        // Initialize the Button for adding a question
        Button btnAddQuestion = findViewById(R.id.btn_add_question);
        btnAddQuestion.setOnClickListener(v -> {
            // Launch the QuestionEditorActivity
            Intent intent = new Intent(ExamEditorActivity.this, QuestionEditorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_QUESTION);
        });

        // Set up the "Save Exam" button click listener
        Button btnSaveExam = findViewById(R.id.btn_save_exam);
        btnSaveExam.setOnClickListener(v -> saveExamToFirestore(examId, examTitle, examDate, examDuration, questionAdapter.getQuestions()));

        // Initialize the floating action button for adding a question
        fabAddQuestion = findViewById(R.id.fab_add_question);
        fabAddQuestion.setOnClickListener(v -> {
            // Launch the QuestionEditorActivity
            Intent intent = new Intent(ExamEditorActivity.this, QuestionEditorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_QUESTION);
        });

        // Initialize the ItemTouchHelper
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // Rearrange the questions in the list
                rearrangeQuestions(fromPosition, toPosition);

                // Notify the adapter about the change
                questionAdapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not needed for drag-and-drop functionality
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(questionRecyclerView);

        // Check if examId is not null before proceeding
        if (examId != null) {
            // Retrieve the exam questions from Firestore
            FirebaseFirestore.getInstance()
                    .collection("exams")
                    .document(examId)
                    .collection("questions")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<Question> questions = new ArrayList<>();
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            Question question = document.toObject(Question.class);
                            questions.add(question);
                        }
                        // Update the adapter with the retrieved questions
                        questionAdapter.setQuestions(questions);
                    })
                    .addOnFailureListener(e -> {
                        // Error retrieving exam questions
                        Toast.makeText(ExamEditorActivity.this, "Failed to retrieve exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // examId is null, handle the error or show an error message
            Toast.makeText(ExamEditorActivity.this, "Invalid exam ID", Toast.LENGTH_SHORT).show();
        }
    }



    private void saveExamToFirestore(String examId, String examTitle, String examDate, int examDuration, List<Question> questions) {
        if (examId != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Create a new Exam object with the provided data and the questions list
            Exam exam = new Exam(examId, examTitle, examDate, examDuration, questions);


            // Convert the Exam object to a Map<String, Object>
            Map<String, Object> examData = new HashMap<>();
            examData.put("title", exam.getTitle());
            examData.put("date", exam.getDate());
            examData.put("duration", exam.getDuration());

            // Update the exam document with the new data
            db.collection("exams")
                    .document(examId)
                    .set(examData)
                    .addOnSuccessListener(aVoid -> {
                        // Exam document updated successfully

                        // Retrieve the existing questions for the exam
                        db.collection("exams")
                                .document(examId)
                                .collection("questions")
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    List<Question> existingQuestions = new ArrayList<>();
                                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                        Question question = document.toObject(Question.class);
                                        existingQuestions.add(question);
                                    }

                                    // Save the questions to Firestore
                                    saveQuestionsToFirestore(examId, questions, existingQuestions);
                                })
                                .addOnFailureListener(e -> {
                                    // Error retrieving existing questions
                                    Log.e("ExamEditorActivity", "Error retrieving existing questions: " + e.getMessage());
                                    Toast.makeText(ExamEditorActivity.this, "Failed to update exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        // Error updating exam document
                        Log.e("ExamEditorActivity", "Error updating exam: " + e.getMessage());
                        Toast.makeText(ExamEditorActivity.this, "Failed to update exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Log.e("ExamEditorActivity", "examId is null, cannot save exam");
            Toast.makeText(ExamEditorActivity.this, "Failed to save exam: examId is null", Toast.LENGTH_SHORT).show();
        }
    }



    private void createNewExam(FirebaseFirestore db, String examTitle, String examDate, int examDuration, List<Question> questions) {
        // Convert the Exam object to a Map<String, Object>
        Map<String, Object> examData = new HashMap<>();
        examData.put("title", examTitle);
        examData.put("date", examDate);
        examData.put("duration", examDuration);

        // Save the exam data to Firestore
        db.collection("exams")
                .add(examData)
                .addOnSuccessListener(documentReference -> {
                    // Exam document created successfully
                    String examId = documentReference.getId();

                    // Create a new Exam object with the examId
                    Exam exam = new Exam(examId, examTitle, examDate, examDuration, questions);

                    // Update the examId field in the Firestore document
                    documentReference.update("examId", examId)
                            .addOnSuccessListener(aVoid -> {
                                // Save the questions to Firestore
                                saveQuestionsToFirestore(examId, questions, new ArrayList<>());
                            })
                            .addOnFailureListener(e -> {
                                // Error updating examId field
                                Log.e("ExamEditorActivity", "Error updating examId: " + e.getMessage());
                                Toast.makeText(ExamEditorActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Error creating exam document
                    Log.e("ExamEditorActivity", "Error creating exam: " + e.getMessage());
                    Toast.makeText(ExamEditorActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void updateExistingExam(FirebaseFirestore db, String examId, String examTitle, String examDate, int examDuration, List<Question> questions) {
        // Update the exam document with the new data
        Map<String, Object> examData = new HashMap<>();
        examData.put("title", examTitle);
        examData.put("date", examDate);
        examData.put("duration", examDuration);

        db.collection("exams")
                .document(examId)
                .set(examData)
                .addOnSuccessListener(aVoid -> {
                    // Exam document updated successfully

                    // Retrieve the existing questions for the exam
                    db.collection("exams")
                            .document(examId)
                            .collection("questions")
                            .get()
                            .addOnSuccessListener(querySnapshot -> {
                                List<Question> existingQuestions = new ArrayList<>();
                                for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                    Question question = document.toObject(Question.class);
                                    existingQuestions.add(question);
                                }

                                // Save the questions to Firestore
                                saveQuestionsToFirestore(examId, questions, existingQuestions);
                            })
                            .addOnFailureListener(e -> {
                                // Error retrieving existing questions
                                Log.e("ExamEditorActivity", "Error retrieving existing questions: " + e.getMessage());
                                Toast.makeText(ExamEditorActivity.this, "Failed to update exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Error updating exam document
                    Log.e("ExamEditorActivity", "Error updating exam: " + e.getMessage());
                    Toast.makeText(ExamEditorActivity.this, "Failed to update exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveQuestionsToFirestore(String examId, List<Question> newQuestions, List<Question> existingQuestions) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        // Delete existing questions
        db.collection("exams")
                .document(examId)
                .collection("questions")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<DocumentSnapshot> documentsToDelete = querySnapshot.getDocuments();
                    for (DocumentSnapshot document : documentsToDelete) {
                        batch.delete(document.getReference());
                    }

                    // Save new questions
                    for (Question newQuestion : newQuestions) {
                        DocumentReference questionRef = db.collection("exams").document(examId).collection("questions").document();
                        batch.set(questionRef, newQuestion);
                    }

                    // Update existing questions
                    for (Question existingQuestion : existingQuestions) {
                        db.collection("exams")
                                .document(examId)
                                .collection("questions")
                                .whereEqualTo("text", existingQuestion.getText())
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (!queryDocumentSnapshots.isEmpty()) {
                                        DocumentSnapshot questionSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                        batch.set(questionSnapshot.getReference(), existingQuestion);
                                    }
                                });
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                // Questions saved/updated successfully
                                Toast.makeText(ExamEditorActivity.this, "Exam saved", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Error saving/updating questions
                                Log.e("ExamEditorActivity", "Error saving/updating questions: " + e.getMessage());
                                Toast.makeText(ExamEditorActivity.this, "Failed to save/update questions: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void rearrangeQuestions(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(questions, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(questions, i, i - 1);
            }
        }

        // Get the exam title from the Intent extras
        String examTitle = getIntent().getStringExtra("examTitle");

        // Update the question order in Firestore
        updateQuestionOrderInFirestore(examTitle, questions);
    }



    private void updateQuestionOrderInFirestore(String examTitle, List<Question> updatedQuestions) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("exams")
                .whereEqualTo("title", examTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot examSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String examId = examSnapshot.getId();

                        // Delete existing questions for the exam
                        db.collection("exams")
                                .document(examId)
                                .collection("questions")
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    List<DocumentSnapshot> documentsToDelete = querySnapshot.getDocuments();
                                    for (DocumentSnapshot document : documentsToDelete) {
                                        document.getReference().delete();
                                    }

                                    // Save the updated questions with the new order
                                    saveQuestionsToFirestore(examId, updatedQuestions, new ArrayList<>());
                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                    Log.e("ExamEditorActivity", "Error deleting existing questions: " + e.getMessage());
                                    Toast.makeText(ExamEditorActivity.this, "Failed to update question order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Log.e("ExamEditorActivity", "Error retrieving exam: " + e.getMessage());
                    Toast.makeText(ExamEditorActivity.this, "Failed to update question order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void loadExamFramework(String examTitle) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exam_frameworks")
                .whereEqualTo("title", examTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Exam framework data exists, populate the questions list
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        List<Map<String, Object>> questionMaps = (List<Map<String, Object>>) documentSnapshot.get("questions");

                        questions = new ArrayList<>();
                        if (questionMaps != null) {
                            for (Map<String, Object> questionMap : questionMaps) {
                                String text = (String) questionMap.get("text");
                                String typeString = (String) questionMap.get("type");
                                QuestionType type = QuestionType.valueOf(typeString);
                                List<String> options = (List<String>) questionMap.get("options");
                                String answer = (String) questionMap.get("answer");
                                Question question = new Question(text, type, options, answer);
                                questions.add(question);
                            }
                        }
                        updateQuestionList();
                    } else {
                        // Exam framework data does not exist
                        Toast.makeText(ExamEditorActivity.this, "Exam framework not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(ExamEditorActivity.this, "Failed to load exam framework: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void showAddQuestionDialog() {
        AddQuestionDialog dialog = new AddQuestionDialog();
        dialog.setListener(this::onQuestionAdded);
        dialog.show(getSupportFragmentManager(), "add_question_dialog");
    }

    private void onQuestionAdded(Question question) {
        questions.add(question);
        questionAdapter.notifyItemInserted(questions.size() - 1);
    }


    private void updateQuestionList() {
        if (questionAdapter == null) {
            questionAdapter = new QuestionAdapter(questions);
            questionRecyclerView.setAdapter(questionAdapter);
            questionAdapter.setOnQuestionClickListener(this); // Set the OnQuestionClickListener
            questionAdapter.setOnQuestionEditListener(this); // Set the OnQuestionEditListener
            questionAdapter.setOnQuestionDeleteListener(this); // Set the OnQuestionDeleteListener
        } else {
            questionAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onQuestionClick(Question question) {
        // Launch the QuestionEditorActivity and pass the Question object
        Intent intent = new Intent(this, QuestionEditorActivity.class);
        intent.putExtra("question", GSON.toJson(question));
        startActivityForResult(intent, REQUEST_CODE_EDIT_QUESTION);
    }

    @Override
    public void onQuestionEdit(Question question) {
        // Launch the QuestionEditorActivity for editing the question
        Intent intent = new Intent(this, QuestionEditorActivity.class);
        intent.putExtra("question", GSON.toJson(question));
        startActivityForResult(intent, REQUEST_CODE_EDIT_QUESTION);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_ADD_QUESTION && resultCode == RESULT_OK) {
            if (data != null) {
                String newQuestionJson = data.getStringExtra("newQuestion");
                if (newQuestionJson != null) {
                    Question newQuestion = GSON.fromJson(newQuestionJson, Question.class);

                    // Add the new question to the questions list
                    questions.add(newQuestion);

                    // Notify the adapter about the new question
                    if (questionAdapter != null) {
                        questionAdapter.notifyItemInserted(questions.size() - 1);
                    }

                    // Get the examId from the Intent extras
                    String examId = getIntent().getStringExtra("examId");

                    // Check if examId is not null before saving the question
                    if (examId != null) {
                        // Save the new question to Firestore
                        List<Question> newQuestions = new ArrayList<>();
                        newQuestions.add(newQuestion);
                        saveQuestionsToFirestore(examId, newQuestions, new ArrayList<>());
                    } else {
                        Log.e("ExamEditorActivity", "examId is null, cannot save question to Firestore");
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE_EDIT_QUESTION && resultCode == RESULT_OK) {
            if (data != null) {
                String updatedQuestionJson = data.getStringExtra("updatedQuestion");
                if (updatedQuestionJson != null) {
                    Question updatedQuestion = GSON.fromJson(updatedQuestionJson, Question.class);

                    // Find the index of the question to be updated
                    int index = questions.indexOf(updatedQuestion);

                    if (index != -1) {
                        // Update the question in the questions list
                        questions.set(index, updatedQuestion);

                        // Notify the adapter about the data change
                        if (questionAdapter != null) {
                            questionAdapter.notifyItemChanged(index);
                        }

                        // Get the examId from the Intent extras
                        String examId = getIntent().getStringExtra("examId");

                        // Check if examId is not null before updating the question
                        if (examId != null) {
                            // Update the question in Firestore
                            List<Question> existingQuestions = new ArrayList<>(questions);
                            existingQuestions.remove(index); // Remove the old question from the list
                            saveQuestionsToFirestore(examId, new ArrayList<>(), existingQuestions);
                        } else {
                            Log.e("ExamEditorActivity", "examId is null, cannot update question in Firestore");
                        }
                    }
                }
            }
        }
    }



    private void updateQuestionInFirestore(String examTitle, Question updatedQuestion) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("exams")
                .whereEqualTo("title", examTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot examSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String examId = examSnapshot.getId();

                        // Find the question document to update
                        db.collection("exams")
                                .document(examId)
                                .collection("questions")
                                .whereEqualTo("text", updatedQuestion.getText())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentSnapshot questionSnapshot = querySnapshot.getDocuments().get(0);
                                        String questionId = questionSnapshot.getId();

                                        // Update the question document
                                        db.collection("exams")
                                                .document(examId)
                                                .collection("questions")
                                                .document(questionId)
                                                .set(updatedQuestion)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Question updated successfully
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle error
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }


    @Override
    public void onQuestionDeleted(Question question) {
        // Get the exam title from the Intent extras
        String examTitle = getIntent().getStringExtra("examTitle");

        // Delete the question from Firestore
        deleteQuestionFromFirestore(examTitle, question);
    }


    private void deleteQuestionFromFirestore(String examTitle, Question question) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("exams")
                .whereEqualTo("title", examTitle)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot examSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String examId = examSnapshot.getId();

                        // Find the question document to delete
                        db.collection("exams")
                                .document(examId)
                                .collection("questions")
                                .whereEqualTo("text", question.getText())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentSnapshot questionSnapshot = querySnapshot.getDocuments().get(0);
                                        String questionId = questionSnapshot.getId();

                                        // Delete the question document
                                        db.collection("exams")
                                                .document(examId)
                                                .collection("questions")
                                                .document(questionId)
                                                .delete()
                                                .addOnSuccessListener(aVoid -> {
                                                    // Question deleted successfully
                                                    int index = questions.indexOf(question);
                                                    if (index != -1) {
                                                        questions.remove(index);
                                                        questionAdapter.notifyItemRemoved(index);
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    // Handle error
                                                });
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    // Handle error
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }



    private void updateExistingQuestion(Question updatedQuestion) {
        // Find the index of the existing question in the questions list
        int index = questions.indexOf(updatedQuestion);

        if (index != -1) {
            // Replace the existing question with the updated question
            questions.set(index, updatedQuestion);
        }
    }


    // In the onClick listener of the "Save" button
    // List<Question> updatedQuestions = new ArrayList<>(questions);
    // updateQuestionOrderInFirestore(updatedQuestions);
}
