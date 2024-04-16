package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class StudentHomepageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homepage);

        // Find the views
        TextView tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        TextView tvAssignments = findViewById(R.id.tv_assignments);
        TextView tvAssignmentsCount = findViewById(R.id.tv_assignments_count);
        TextView tvUpcomingExams = findViewById(R.id.tv_upcoming_exams);
        TextView tvUpcomingExamsCount = findViewById(R.id.tv_upcoming_exams_count);
        TextView tvGrades = findViewById(R.id.tv_grades);
        TextView tvGradesCount = findViewById(R.id.tv_grades_count);

        Button btnViewAssignments = findViewById(R.id.btn_view_assignments);
        Button btnViewExams = findViewById(R.id.btn_view_exams);
        Button btnViewGrades = findViewById(R.id.btn_view_grades);
        Button btnSettings = findViewById(R.id.btn_settings);
        Button btnLogOut = findViewById(R.id.btn_log_out);

        // Set up click listeners for the buttons
        btnViewAssignments.setOnClickListener(v -> {
            // Handle view assignments button click
        });

        btnViewExams.setOnClickListener(v -> {
            // Handle view exams button click
        });

        btnViewGrades.setOnClickListener(v -> {
            // Handle view grades button click
        });

        btnSettings.setOnClickListener(v -> {
            // Handle settings button click
        });

        btnLogOut.setOnClickListener(v -> {
            // Handle log out button click
        });
    }
}
