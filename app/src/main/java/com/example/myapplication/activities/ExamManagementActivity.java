package com.example.myapplication.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExamListAdapter;
import com.example.myapplication.models.Exam;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ExamManagementActivity extends AppCompatActivity {

    private RecyclerView rvExamList;
    private ExamListAdapter examListAdapter;
    private List<Exam> examList;

    private EditText etExamTitle;
    private EditText etExamDate;
    private EditText etExamDuration;
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
        loadExamList();

        // Set up Create Exam
        btnCreateExam.setOnClickListener(v -> {
            String examTitle = etExamTitle.getText().toString();
            String examDate = etExamDate.getText().toString();
            int examDuration = Integer.parseInt(etExamDuration.getText().toString());
            createExam(examTitle, examDate, examDuration);
        });
    }

    private void loadExamList() {
        // Fetch exam data from Firestore and update the exam list
        db.collection("exams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    examList.clear();
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots.getDocuments()) {
                        Exam exam = snapshot.toObject(Exam.class);
                        examList.add(exam);
                    }
                    examListAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void createExam(String title, String date, int duration) {
        // Create a new exam in Firestore
        Exam newExam = new Exam(title, date, duration);
        db.collection("exams")
                .add(newExam)
                .addOnSuccessListener(documentReference -> {
                    // Exam created successfully
                    examList.add(newExam);
                    examListAdapter.notifyItemInserted(examList.size() - 1);
                    clearCreateExamFields();
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }

    private void clearCreateExamFields() {
        etExamTitle.setText("");
        etExamDate.setText("");
        etExamDuration.setText("");
    }
}
