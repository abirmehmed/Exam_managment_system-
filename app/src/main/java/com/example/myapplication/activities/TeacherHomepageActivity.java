package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class TeacherHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_homepage);

        // Find the views
        TextView tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        TextView tvExamsCreated = findViewById(R.id.tv_exams_created);
        TextView tvExamsCreatedCount = findViewById(R.id.tv_exams_created_count);
        TextView tvUpcomingExams = findViewById(R.id.tv_upcoming_exams);
        TextView tvUpcomingExamsCount = findViewById(R.id.tv_upcoming_exams_count);
        TextView tvPendingEvaluations = findViewById(R.id.tv_pending_evaluations);
        TextView tvPendingEvaluationsCount = findViewById(R.id.tv_pending_evaluations_count);

        Button btnExamManagement = findViewById(R.id.btn_exam_management);
        Button btnStudentEvaluation = findViewById(R.id.btn_student_evaluation);
        Button btnCommunication = findViewById(R.id.btn_communication);
        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnLogOut = findViewById(R.id.btn_log_out);

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

        btnSettings.setOnClickListener(v -> {
            // Handle settings button click
        });

        btnLogOut.setOnClickListener(v -> {
            // Handle log out button click
        });
    }
}
