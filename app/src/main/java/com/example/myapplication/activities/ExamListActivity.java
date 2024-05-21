package com.example.myapplication.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExamListManager;
import com.example.myapplication.models.Exam;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.app.Activity;


public class ExamListActivity extends AppCompatActivity {

    private RecyclerView rvExamList;
    private ExamListManager examListManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_list);

        rvExamList = findViewById(R.id.rv_exam_list);

        // Fetch exams from Firebase Firestore
        fetchExamsFromFirestore();
    }

    private void fetchExamsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("exams")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Exam> examList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Exam exam = document.toObject(Exam.class);
                        examList.add(exam);
                    }

                    // Initialize the ExamListManager
                    examListManager = new ExamListManager(this, examList);

                    // Set up the RecyclerView with the ExamListAdapter
                    rvExamList.setLayoutManager(new LinearLayoutManager(this));
                    rvExamList.setAdapter(examListManager.getExamListAdapter());
                })
                .addOnFailureListener(e -> {
                    // Handle error
                });
    }
    public void onExamClick(Exam exam) {
        Intent intent = new Intent(this, ExamActivity.class);
        intent.putExtra("examId", exam.getId());
        startActivity(intent);
    }

}
