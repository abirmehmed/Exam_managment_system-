package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.content.Intent;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;



import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionEditorActivity extends AppCompatActivity {
    private static final Gson GSON = new Gson();
    private Question question;
    private DocumentReference questionDocRef;

    private EditText etQuestionText;
    private Spinner spinnerQuestionType;
    private LinearLayout llQuestionOptions;
    private EditText etQuestionAnswer;
    private Button btnSaveQuestion;
    private Button btnDeleteQuestion;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_editor);

        // Find views
        etQuestionText = findViewById(R.id.et_question_text);
        spinnerQuestionType = findViewById(R.id.spinner_question_type);
        llQuestionOptions = findViewById(R.id.ll_question_options);
        etQuestionAnswer = findViewById(R.id.et_question_answer);
        btnSaveQuestion = findViewById(R.id.btn_save_question);
        btnDeleteQuestion = findViewById(R.id.btn_delete_question);

        // Set up the question type spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.question_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuestionType.setAdapter(adapter);

        // Set up the question type spinner listener
        spinnerQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateUIForQuestionType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Check if the activity was launched for adding a new question or editing an existing one
        String questionJson = getIntent().getStringExtra("question");
        if (questionJson != null) {
            // Editing an existing question
            question = GSON.fromJson(questionJson, Question.class);
            initializeUI();
        } else {
            // Adding a new question
            question = null;
        }

        // Set the "Save" button click listener
        btnSaveQuestion.setOnClickListener(v -> saveQuestion());

        // Set the "Delete" button click listener
        if (question != null) {
            // Only show the "Delete" button if editing an existing question
            btnDeleteQuestion.setOnClickListener(v -> deleteQuestion());
        } else {
            // Hide the "Delete" button if adding a new question
            btnDeleteQuestion.setVisibility(View.GONE);
        }
    }

    private void updateUIForQuestionType(int position) {
        switch (position) {
            case 0: // Multiple Choice
                llQuestionOptions.setVisibility(View.VISIBLE);
                etQuestionAnswer.setVisibility(View.VISIBLE);
                // Add EditText views for options dynamically
                llQuestionOptions.removeAllViews(); // Clear any existing options
                for (int i = 0; i < 4; i++) { // Add 4 options
                    EditText optionEditText = new EditText(this);
                    optionEditText.setHint("Option " + (i + 1));
                    llQuestionOptions.addView(optionEditText);
                }
                break;
            case 1: // True/False
                llQuestionOptions.setVisibility(View.VISIBLE);
                etQuestionAnswer.setVisibility(View.VISIBLE);
                // Add two EditText views for True and False options
                llQuestionOptions.removeAllViews(); // Clear any existing options
                EditText trueOptionEditText = new EditText(this);
                trueOptionEditText.setText("True");
                llQuestionOptions.addView(trueOptionEditText);
                EditText falseOptionEditText = new EditText(this);
                falseOptionEditText.setText("False");
                llQuestionOptions.addView(falseOptionEditText);
                break;
            case 2: // Short Answer
                llQuestionOptions.setVisibility(View.GONE);
                etQuestionAnswer.setVisibility(View.VISIBLE);
                break;
        }
    }




    private void initializeUI() {
        // Populate the UI with the question data
        etQuestionText.setText(question.getText());

        // Set the selected question type in the spinner
        int questionTypePosition = getQuestionTypePosition(question.getType());
        spinnerQuestionType.setSelection(questionTypePosition);

        // Initialize other UI elements based on the question type and options
        initializeQuestionOptions();
        initializeQuestionAnswer();
    }


    private int getQuestionTypePosition(QuestionType questionType) {
        // Map the QuestionType to the corresponding position in the spinner
        switch (questionType) {
            case MULTIPLE_CHOICE:
                return 0;
            case TRUE_FALSE:
                return 1;
            case SHORT_ANSWER:
                return 2;
            default:
                return 0;
        }
    }

    private void initializeQuestionOptions() {
        llQuestionOptions.removeAllViews(); // Clear any existing options

        if (question.getType() == QuestionType.MULTIPLE_CHOICE || question.getType() == QuestionType.TRUE_FALSE) {
            llQuestionOptions.setVisibility(View.VISIBLE);

            List<String> options = question.getOptions();
            for (String option : options) {
                EditText optionEditText = new EditText(this);
                optionEditText.setText(option);
                llQuestionOptions.addView(optionEditText);
            }
        } else {
            llQuestionOptions.setVisibility(View.GONE);
        }
    }


    private void initializeQuestionAnswer() {
        if (question.getType() == QuestionType.SHORT_ANSWER) {
            etQuestionAnswer.setVisibility(View.VISIBLE);
            etQuestionAnswer.setText(question.getAnswer());
        } else {
            etQuestionAnswer.setVisibility(View.GONE);
            // Hide the correct answer for multiple-choice and true/false questions
            etQuestionAnswer.setText("");
        }
    }


    private List<String> getOptionsFromUI() {
        List<String> options = new ArrayList<>();
        for (int i = 0; i < llQuestionOptions.getChildCount(); i++) {
            View view = llQuestionOptions.getChildAt(i);
            if (view instanceof EditText) {
                String option = ((EditText) view).getText().toString().trim();
                if (!option.isEmpty()) {
                    options.add(option);
                }
            }
        }
        return options;
    }

    private void saveQuestion() {
        // Get the updated question data from the UI elements
        String updatedQuestionText = etQuestionText.getText().toString().trim();
        QuestionType updatedQuestionType = QuestionType.values()[spinnerQuestionType.getSelectedItemPosition()];
        List<String> updatedOptions = getOptionsFromUI();
        String updatedAnswer = etQuestionAnswer.getText().toString().trim();

        // Create a new Question object with the updated data
        Question updatedQuestion = new Question(updatedQuestionText, updatedQuestionType, updatedOptions, updatedAnswer);

        // Create a new Intent to pass the result back
        Intent resultIntent = new Intent();

        if (question == null) {
            // Adding a new question
            resultIntent.putExtra("newQuestion", GSON.toJson(updatedQuestion));
        } else {
            // Editing an existing question
            resultIntent.putExtra("updatedQuestion", GSON.toJson(updatedQuestion));
        }

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void saveQuestionToFirestore(Question question) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> questionData = new HashMap<>();
        questionData.put("text", question.getText());
        questionData.put("type", question.getType().name());
        questionData.put("options", question.getOptions());
        questionData.put("answer", question.getAnswer());

        if (questionDocRef == null) {
            // Create a new document in the "questions" collection
            db.collection("questions")
                    .add(questionData)
                    .addOnSuccessListener(documentReference -> {
                        questionDocRef = documentReference;
                        Toast.makeText(QuestionEditorActivity.this, "Question saved successfully", Toast.LENGTH_SHORT).show();
                        setResultAndFinish(question); // Set the result and finish the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(QuestionEditorActivity.this, "Failed to save question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            // Update the existing document
            questionDocRef.set(questionData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(QuestionEditorActivity.this, "Question updated successfully", Toast.LENGTH_SHORT).show();
                        setResultAndFinish(question); // Set the result and finish the activity
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(QuestionEditorActivity.this, "Failed to update question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void setResultAndFinish(Question question) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("updatedQuestion", GSON.toJson(question));
        setResult(RESULT_OK, resultIntent);
        finish();
    }


    private void deleteQuestion() {
        if (questionDocRef != null) {
            questionDocRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(QuestionEditorActivity.this, "Question deleted successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity after successful deletion
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(QuestionEditorActivity.this, "Failed to delete question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(QuestionEditorActivity.this, "No question to delete", Toast.LENGTH_SHORT).show();
        }
    }




}
