package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExamListAdapter;
import com.example.myapplication.models.Exam;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.models.MyApplication;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.View;


public class ExamManagementActivity extends AppCompatActivity implements ExamListAdapter.OnExamClickListener {

    private EditText etExamTitle;
    private EditText etExamDate;
    private EditText etExamDuration;
    private RecyclerView rvExamList;
    private ExamListAdapter examListAdapter;
    private List<Exam> examList;

    private Button btnCreateExam;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_management);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Find views
        rvExamList = findViewById(R.id.rv_exam_list);
        etExamTitle = findViewById(R.id.et_exam_title);
        etExamDate = findViewById(R.id.et_exam_date);
        etExamDuration = findViewById(R.id.et_exam_duration);
        btnCreateExam = findViewById(R.id.btn_create_exam);

        // Set up Exam List
        examList = new ArrayList<>();
        examListAdapter = new ExamListAdapter(examList);
        rvExamList.setLayoutManager(new LinearLayoutManager(this));
        rvExamList.setAdapter(examListAdapter);
        examListAdapter.setOnExamClickListener(this); // Set the OnExamClickListener
        loadExamList();

        // Set up Create Exam
        btnCreateExam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String examTitle = etExamTitle.getText().toString().trim();
                String examDate = etExamDate.getText().toString().trim();
                String examDurationText = etExamDuration.getText().toString().trim();

                if (examTitle.isEmpty() || examDate.isEmpty() || examDurationText.isEmpty()) {
                    Toast.makeText(ExamManagementActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                int examDuration = Integer.parseInt(examDurationText);
                createExam(examTitle, examDate, examDuration);

                // Increment the exams created count
                incrementExamsCreatedCount();
            }
        });
    }

    private void incrementExamsCreatedCount() {
        // Get a reference to the TeacherHomepageActivity instance
        TeacherHomepageActivity teacherHomepageActivity = MyApplication.getTeacherHomepageActivity();

        // Call the incrementExamsCreatedCount method in the TeacherHomepageActivity
        if (teacherHomepageActivity != null) {
            teacherHomepageActivity.incrementExamsCreatedCount();
        }
    }

    private void createAndLaunchExamEditor(String examTitle, String examDate, int examDuration) {
        // Create a new exam document in Firestore
        Map<String, Object> examData = new HashMap<>();
        examData.put("title", examTitle);
        examData.put("date", examDate);
        examData.put("duration", examDuration);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exams")
                .add(examData)
                .addOnSuccessListener(documentReference -> {
                    // Exam document created successfully
                    String examId = documentReference.getId();

                    // Pass the exam data and examId to the ExamEditorActivity
                    Intent intent = new Intent(ExamManagementActivity.this, ExamEditorActivity.class);
                    intent.putExtra("examTitle", examTitle);
                    intent.putExtra("examDate", examDate);
                    intent.putExtra("examDuration", examDuration);
                    intent.putExtra("examId", examId);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    // Error creating exam document
                    Toast.makeText(ExamManagementActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void loadExamList() {
        // Fetch exam data from Firestore and update the exam list
        db.collection("exams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int oldSize = examList.size();
                    examList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Exam exam = snapshot.toObject(Exam.class);
                        examList.add(exam);
                    }
                    examListAdapter.notifyItemRangeInserted(oldSize, examList.size() - oldSize);
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(this, "Failed to load exam list: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void createExam(String title, String date, int duration) {
        // Create a new exam in Firestore
        Exam newExam = new Exam(title, date, duration);
        db.collection("exams")
                .add(newExam)
                .addOnSuccessListener(documentReference -> {
                    // Exam created successfully
                    String examId = documentReference.getId();
                    newExam.setId(examId);
                    int newItemIndex = examList.size();
                    examList.add(newExam);
                    examListAdapter.notifyItemInserted(newItemIndex);
                    clearCreateExamFields();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                    Toast.makeText(ExamManagementActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


    private void clearCreateExamFields() {
        etExamTitle.setText("");
        etExamDate.setText("");
        etExamDuration.setText("");
    }

    @Override
    public void onExamClick(Exam exam) {
        // Launch the ExamEditorActivity and pass the exam data
        Intent intent = new Intent(this, ExamEditorActivity.class);
        intent.putExtra("examTitle", exam.getTitle());
        intent.putExtra("examDate", exam.getDate());
        intent.putExtra("examDuration", exam.getDuration());
        intent.putExtra("examId", exam.getId());
        startActivity(intent);
    }
}
