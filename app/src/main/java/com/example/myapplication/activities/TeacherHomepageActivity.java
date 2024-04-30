package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class TeacherHomepageActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private TextView tvExamsCreated;
    private TextView tvExamsCreatedCount;
    private TextView tvPendingEvaluations;
    private TextView tvPendingEvaluationsCount;
    private TextView tvTotalStudents;
    private TextView tvTotalStudentsCount;

    private Button btnExamManagement;
    private Button btnStudentEvaluation;
    private Button btnCommunication;
    private Button btnNotifications;
    private Button btnSettings;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_homepage);

        // Find the views
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        tvExamsCreated = findViewById(R.id.tv_exams_created);
        tvExamsCreatedCount = findViewById(R.id.tv_exams_created_count);
        tvPendingEvaluations = findViewById(R.id.tv_pending_evaluations);
        tvPendingEvaluationsCount = findViewById(R.id.tv_pending_evaluations_count);
        tvTotalStudents = findViewById(R.id.tv_total_students);
        tvTotalStudentsCount = findViewById(R.id.tv_total_students_count);

        btnExamManagement = findViewById(R.id.btn_exam_management);
        btnStudentEvaluation = findViewById(R.id.btn_student_evaluation);
        btnCommunication = findViewById(R.id.btn_communication);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnSettings = findViewById(R.id.btn_settings);
        btnLogOut = findViewById(R.id.btn_log_out);

        // Set up click listeners for the buttons
        btnExamManagement.setOnClickListener(v -> {
            // Handle exam management button click
        });

        btnStudentEvaluation.setOnClickListener(v -> {
            // Handle student evaluation button click
        });

        btnCommunication.setOnClickListener(v -> {
            // Handle communication button click
        });

        btnNotifications.setOnClickListener(v -> {
            // Handle notifications button click
        });

        btnSettings.setOnClickListener(v -> {
            // Handle settings button click
        });

        btnLogOut.setOnClickListener(v -> {
            // Handle log out button click
        });
    }
}
