package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import android.net.Uri;

import android.widget.ImageView;

import androidx.annotation.Nullable;
import com.example.myapplication.models.MyApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.myapplication.activities.ProfilePictureUtils;

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
        private static final int REQUEST_IMAGE_PICK = 1;
        private static final int REQUEST_IMAGE_CAPTURE = 2;

        private ImageView ivProfilePicture;
        private FirebaseUser currentUser;

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
                startExamManagementActivity();
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
                logOut();
            });

            ivProfilePicture = findViewById(R.id.iv_profile_picture);
            currentUser = FirebaseAuth.getInstance().getCurrentUser();

            ivProfilePicture.setOnClickListener(v -> openImagePicker());

            // Set the static reference to the TeacherHomepageActivity instance
            MyApplication.setTeacherHomepageActivity(this);
        }

        public void incrementExamsCreatedCount() {
            int currentCount = Integer.parseInt(tvExamsCreatedCount.getText().toString());
            int newCount = currentCount + 1;
            tvExamsCreatedCount.setText(String.valueOf(newCount));
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

        private void startExamManagementActivity() {
            // Start the ExamManagementActivity
            Intent intent = new Intent(TeacherHomepageActivity.this, ExamManagementActivity.class);
            startActivity(intent);
        }

        private void logOut() {
            // Clear any user session or authentication data
            // ...

            // Start the LoginActivity
            Intent intent = new Intent(TeacherHomepageActivity.this, LoginActivity.class);
            startActivity(intent);

            // Finish the current activity to prevent going back to it
            finish();
        }
    }

