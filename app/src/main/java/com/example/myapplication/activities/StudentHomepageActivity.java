package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class StudentHomepageActivity extends AppCompatActivity {

    private TextView tvWelcomeMessage;
    private TextView tvAttendance;
    private TextView tvAttendanceCount;
    private TextView tvExams;
    private TextView tvExamsCount;
    private TextView tvGrades;
    private TextView tvGradesCount;

    private Button btnViewAttendance;
    private Button btnViewExams;
    private Button btnViewGrades;
    private Button btnSettings;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_homepage);

        // Find the views
        tvWelcomeMessage = findViewById(R.id.tv_welcome_message);
        tvAttendance = findViewById(R.id.tv_attendance);
        tvAttendanceCount = findViewById(R.id.tv_attendance_count);
        tvExams = findViewById(R.id.tv_exams);
        tvExamsCount = findViewById(R.id.tv_exams_count);
        tvGrades = findViewById(R.id.tv_grades);
        tvGradesCount = findViewById(R.id.tv_grades_count);

        btnViewAttendance = findViewById(R.id.btn_view_attendance);
        btnViewExams = findViewById(R.id.btn_view_exams);
        btnViewGrades = findViewById(R.id.btn_view_grades);
        btnSettings = findViewById(R.id.btn_settings);
        btnLogOut = findViewById(R.id.btn_log_out);

        // Set up click listeners for the buttons
        btnViewAttendance.setOnClickListener(v -> {
            // Handle view attendance button click
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
