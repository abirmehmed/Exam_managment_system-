package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.QuestionAdapter;
import com.example.myapplication.activities.AddQuestionDialog;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExamEditorActivity extends AppCompatActivity {
    private RecyclerView questionRecyclerView;
    private QuestionAdapter questionAdapter;
    private List<Question> questions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_editor);

        // Initialize views
        questionRecyclerView = findViewById(R.id.rv_questions);
        questionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Get the exam framework data from the intent
        String examFrameworkId = getIntent().getStringExtra("examFrameworkId");

        // Load the exam framework data from Firestore
        loadExamFramework(examFrameworkId);

        // Set up the floating action button
        FloatingActionButton fabAddQuestion = findViewById(R.id.fab_add_question);
        fabAddQuestion.setOnClickListener(v -> showAddQuestionDialog());
    }

    private void loadExamFramework(String examFrameworkId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exam_frameworks")
                .document(examFrameworkId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Exam framework data exists, populate the questions list
                        List<Map<String, Object>> questionMaps = (List<Map<String, Object>>) documentSnapshot.get("questions");
                        questions = new ArrayList<>();
                        for (Map<String, Object> questionMap : questionMaps) {
                            String text = (String) questionMap.get("text");
                            QuestionType type = QuestionType.valueOf((String) questionMap.get("type"));
                            List<String> options = (List<String>) questionMap.get("options");
                            String answer = (String) questionMap.get("answer");
                            Question question = new Question(text, type, options, answer);
                            questions.add(question);
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
        // Add the new question to the questions list
        questions.add(question);

        // Update the QuestionAdapter
        updateQuestionList();
    }

    private void updateQuestionList() {
        if (questionAdapter == null) {
            questionAdapter = new QuestionAdapter(questions);
            questionRecyclerView.setAdapter(questionAdapter);
        } else {
            questionAdapter.notifyDataSetChanged();
        }
    }

    // Other methods for handling question operations (add, edit, delete, rearrange)
    // ...
}
