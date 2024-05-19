package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.google.gson.Gson;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;
import android.widget.ImageButton;


import com.example.myapplication.adapters.QuestionAdapter;
import com.example.myapplication.managers.ExamManager;
import com.example.myapplication.managers.QuestionManager;
import com.example.myapplication.models.Question;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.example.myapplication.managers.FirestoreManager;


import java.util.ArrayList;
import java.util.List;

public class ExamEditorActivity extends AppCompatActivity implements QuestionAdapter.OnQuestionClickListener, QuestionAdapter.OnQuestionEditListener, QuestionAdapter.OnQuestionDeleteListener {
    private static final int REQUEST_CODE_ADD_QUESTION = 1;
    private static final int REQUEST_CODE_EDIT_QUESTION = 2;

    private RecyclerView questionRecyclerView;
    private QuestionAdapter questionAdapter;
    private QuestionManager questionManager;
    private ExamManager examManager;
    private ItemTouchHelper itemTouchHelper;
    private FloatingActionButton fabAddQuestion;
    private static final Gson GSON = new Gson();
    private ListenerRegistration questionsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_editor);

        // Retrieve the exam document ID from the Intent extras
        String examId = getIntent().getStringExtra("examId");

        // Fetch the exam details from Firebase using the document ID
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exams")
                .document(examId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String examTitle = documentSnapshot.getString("title");
                        String examDate = documentSnapshot.getString("date");
                        int examDuration = documentSnapshot.getLong("duration").intValue();

                        // Initialize the ExamManager and QuestionManager with the retrieved data
                        List<Question> questions = new ArrayList<>();
                        examManager = new ExamManager(examId, examTitle, examDate, examDuration, questions);
                        questionManager = new QuestionManager(questions);

                        // Set the exam title to the TextView
                        TextView tvExamTitle = findViewById(R.id.tv_exam_title);
                        tvExamTitle.setText(examTitle);

                        // Set up the RecyclerView and adapter
                        setupRecyclerView();

                        // Set up the question adapter listeners
                        questionAdapter.setOnQuestionClickListener(this);
                        questionAdapter.setOnQuestionEditListener(this);
                        questionAdapter.setOnQuestionDeleteListener(this);

                        // Set up the "Add Question" button
                        setupAddQuestionButton();

                        // Set up the "Save Exam" button
                        setupSaveExamButton();

                        // Set up the floating action button for adding a question
                        setupFloatingActionButton();

                        // Set up the ItemTouchHelper for drag-and-drop functionality
                        setupItemTouchHelper();

                        // Set up real-time updates for questions
                        setupQuestionsListener(examId);
                    } else {
                        // Handle the case where the document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                });
    }


    private void setupQuestionsListener(String examId) {
        questionsListener = FirestoreManager.getInstance().getDb()
                .collection("exams")
                .document(examId)
                .collection("questions")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        // Handle error
                        return;
                    }

                    List<Question> questions = new ArrayList<>();
                    for (DocumentSnapshot document : value.getDocuments()) {
                        Question question = document.toObject(Question.class);
                        questions.add(question);
                    }

                    // Update the QuestionManager and the adapter
                    questionManager.setQuestions(questions);
                    questionAdapter.setQuestions(questions);
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove the listener when the activity is destroyed
        if (questionsListener != null) {
            questionsListener.remove();
        }
    }



    private void setupRecyclerView() {
        questionRecyclerView = findViewById(R.id.rv_questions);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter = new QuestionAdapter(questionManager.getQuestions());
        questionRecyclerView.setAdapter(questionAdapter);
    }

    private void setupAddQuestionButton() {
        ImageButton btnAddQuestion = findViewById(R.id.btn_add_question);
        btnAddQuestion.setOnClickListener(v -> {
            // Launch the QuestionEditorActivity
            Intent intent = new Intent(ExamEditorActivity.this, QuestionEditorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_QUESTION);
        });
    }

    private void setupSaveExamButton() {
        ImageButton btnSaveExam = findViewById(R.id.btn_save_exam);
        btnSaveExam.setOnClickListener(v -> {
            // Save the exam to Firestore
            examManager.updateExistingExam(
                    examManager.getExamId(),
                    examManager.getExamTitle(),
                    examManager.getExamDate(),
                    examManager.getExamDuration(),
                    questionManager.getQuestions(),
                    new ExamManager.OnExamUpdatedListener() {
                        @Override
                        public void onExamUpdated() {
                            // Exam updated successfully
                            Toast.makeText(ExamEditorActivity.this, "Exam saved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onExamUpdateFailed(Exception e) {
                            // Error updating exam
                            Toast.makeText(ExamEditorActivity.this, "Failed to save exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    private void setupDeleteExamButton() {
        ImageButton btnDeleteExam = findViewById(R.id.btn_delete_exam);
        btnDeleteExam.setOnClickListener(v -> {
            // Handle delete exam action
            // ...
        });
    }


    private void setupFloatingActionButton() {
        fabAddQuestion = findViewById(R.id.fab_add_question);
        fabAddQuestion.setOnClickListener(v -> {
            // Launch the QuestionEditorActivity
            Intent intent = new Intent(ExamEditorActivity.this, QuestionEditorActivity.class);
            startActivityForResult(intent, REQUEST_CODE_ADD_QUESTION);
        });
    }

    private void setupItemTouchHelper() {
        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                // Rearrange the questions in the list
                questionManager.rearrangeQuestions(fromPosition, toPosition);

                // Notify the adapter about the change
                questionAdapter.notifyItemMoved(fromPosition, toPosition);

                // Update the question order in Firestore
                questionManager.updateQuestionOrderInFirestore(
                        examManager.getExamId(),
                        examManager.getExamTitle(),
                        examManager.getExamDate(),
                        examManager.getExamDuration(),
                        new QuestionManager.OnQuestionOrderUpdatedListener() {
                            @Override
                            public void onQuestionOrderUpdated() {
                                // Question order updated successfully
                            }

                            @Override
                            public void onQuestionOrderUpdateFailed(Exception e) {
                                // Error updating question order
                                Toast.makeText(ExamEditorActivity.this, "Failed to update question order: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );

                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                // Not needed for drag-and-drop functionality
            }
        });

        // Attach the ItemTouchHelper to the RecyclerView
        itemTouchHelper.attachToRecyclerView(questionRecyclerView);
    }

    private void retrieveExamQuestions(String examId) {
        FirestoreManager.getInstance().retrieveExamQuestions(examId, new FirestoreManager.OnQuestionsRetrievedListener() {
            @Override
            public void onQuestionsRetrieved(List<Question> questions) {
                // Update the QuestionManager with the retrieved questions
                questionManager.setQuestions(questions);

                // Update the adapter with the retrieved questions
                questionAdapter.setQuestions(questions);
            }

            @Override
            public void onQuestionsRetrievalFailed(Exception e) {
                // Error retrieving exam questions
                Toast.makeText(ExamEditorActivity.this, "Failed to retrieve exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void onQuestionClick(Question question) {
        // Launch the QuestionEditorActivity and pass the Question object
        Intent intent = new Intent(this, QuestionEditorActivity.class);
        intent.putExtra("question", GSON.toJson(question));
        startActivityForResult(intent, REQUEST_CODE_EDIT_QUESTION);
    }


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

                    // Add the new question to the QuestionManager
                    questionManager.addQuestion(newQuestion);

                    // Notify the adapter about the new question
                    questionAdapter.notifyItemInserted(questionManager.getQuestions().size() - 1);

                    // Update the exam in Firestore with the new question
                    List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
                    FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions, new FirestoreManager.OnQuestionsSavedListener() {
                        @Override
                        public void onQuestionsSaved() {
                            // Handle successful save
                            Toast.makeText(ExamEditorActivity.this, "Questions saved successfully", Toast.LENGTH_SHORT).show();
                            // Navigate to the Exam Management Activity or perform any other necessary actions
                        }

                        @Override
                        public void onSaveFailed(Exception e) {
                            // Handle save failure
                            Log.e("SaveQuestions", "Failed to save questions: " + e.getMessage());
                            // Display an error message or perform any other necessary actions
                        }
                    });

                    // Refresh the RecyclerView
                    questionRecyclerView.setAdapter(questionAdapter);
                    // OR
                    // questionAdapter.notifyDataSetChanged();
                }
            }
        } else if (requestCode == REQUEST_CODE_EDIT_QUESTION && resultCode == RESULT_OK) {
            if (data != null) {
                String updatedQuestionJson = data.getStringExtra("updatedQuestion");
                if (updatedQuestionJson != null) {
                    Question updatedQuestion = GSON.fromJson(updatedQuestionJson, Question.class);

                    int index = questionManager.getQuestions().indexOf(updatedQuestion);

                    if (index != -1) {
                        questionManager.updateQuestion(index, updatedQuestion);
                        questionAdapter.notifyItemChanged(index);

                        List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
                        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions, new FirestoreManager.OnQuestionsSavedListener() {
                            @Override
                            public void onQuestionsSaved() {
                                // Handle successful save
                                Toast.makeText(ExamEditorActivity.this, "Questions saved successfully", Toast.LENGTH_SHORT).show();
                                // Navigate to the Exam Management Activity or perform any other necessary actions
                            }

                            @Override
                            public void onSaveFailed(Exception e) {
                                // Handle save failure
                                Log.e("SaveQuestions", "Failed to save questions: " + e.getMessage());
                                // Display an error message or perform any other necessary actions
                            }
                        });

                        questionRecyclerView.setAdapter(questionAdapter);
                        // OR
                        // questionAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }


    public void onQuestionDeleted(Question question) {
        int index = questionManager.getQuestions().indexOf(question);
        if (index != -1) {
            questionManager.deleteQuestion(index);
            questionAdapter.notifyItemRemoved(index);
            FirestoreManager.getInstance().deleteQuestionFromFirestore(examManager.getExamId(), question.getDocumentId());
            questionManager.getQuestions().remove(question);

            FirestoreManager.getInstance().updateExistingExam(
                    examManager.getExamId(),
                    examManager.getExamTitle(),
                    examManager.getExamDate(),
                    examManager.getExamDuration(),
                    questionManager.getQuestions(),
                    new FirestoreManager.OnQuestionsSavedListener() {
                        @Override
                        public void onQuestionsSaved() {
                            Toast.makeText(ExamEditorActivity.this, "Questions saved successfully", Toast.LENGTH_SHORT).show();
                            // Handle successful save, navigate to the Exam Management Activity or perform other actions
                        }

                        @Override
                        public void onSaveFailed(Exception e) {
                            Log.e("SaveQuestions", "Failed to save questions: " + e.getMessage());
                            // Handle save failure, display an error message, or perform other actions
                        }
                    }
            );
        }
    }


}
