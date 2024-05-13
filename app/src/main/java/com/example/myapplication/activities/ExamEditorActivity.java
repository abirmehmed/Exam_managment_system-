package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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
import com.google.firebase.firestore.QuerySnapshot;
import android.view.View;
import android.content.Intent;
import android.widget.Button;
import android.view.View;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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

        // Initialize the RecyclerView
        questionRecyclerView = findViewById(R.id.rv_questions);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the questions list
        questions = new ArrayList<>();

        // Initialize the questionAdapter
        questionAdapter = new QuestionAdapter(questions);
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
                    }
                }
            }
        }
    }


    @Override
    public void onQuestionDeleted(Question question) {
        // Delete the question from Firestore
        deleteQuestionFromFirestore(question);
    }

    private void deleteQuestionFromFirestore(Question question) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // TODO: Implement the logic to delete the question document from Firestore
        // After successful deletion, remove the question from the questions list and notify the adapter
        int index = questions.indexOf(question);
        if (index != -1) {
            questions.remove(index);
            questionAdapter.notifyItemRemoved(index);
        }
    }

    private void updateQuestionOrderInFirestore(List<Question> updatedQuestions) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // TODO: Implement the logic to update the question order in the Firestore document
        // You can use the updatedQuestions list to update the order of questions in the Firestore document
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