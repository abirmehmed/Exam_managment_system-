package com.example.myapplication.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.activities.ProfilePictureUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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

    private static final int REQUEST_IMAGE_PICK = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView ivProfilePicture;
    private FirebaseUser currentUser;

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

        ivProfilePicture = findViewById(R.id.iv_profile_picture);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        ivProfilePicture.setOnClickListener(v -> openImagePicker());

        // Set up click listeners for the buttons
        btnViewAttendance.setOnClickListener(v -> {
            // Handle view attendance button click
        });

        btnViewExams.setOnClickListener(v -> {
            // Launch the ExamListActivity
            Intent intent = new Intent(StudentHomepageActivity.this, ExamListActivity.class);
            startActivity(intent);
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


    private void openImagePicker() {
        // Open the image picker dialog
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        // Implement camera functionality if needed
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                ProfilePictureUtils.uploadProfilePicture(this, imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                // Handle the captured image if needed
            }
        }
    }
}
