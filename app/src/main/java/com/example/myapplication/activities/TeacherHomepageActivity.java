package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import android.util.Log;
import com.example.myapplication.models.MyApplication;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;


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
    private StorageReference storageReference;

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
        storageReference = FirebaseStorage.getInstance().getReference();

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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image Source");
        builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
            switch (which) {
                case 0:
                    openGallery();
                    break;
                case 1:
                    openCamera();
                    break;
            }
        });
        builder.create().show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = createImageFile();
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        // Implement this method to create a temporary file for storing the captured image
        // Return the created file or null if an error occurs
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_PICK) {
                Uri imageUri = data.getData();
                uploadImageToFirebaseStorage(imageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                File photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                    uploadImageToFirebaseStorage(photoURI);
                }
            }
        }
    }
    private void uploadImageToFirebaseStorage(Uri imageUri) {
        FirebaseStorage.getInstance().setMaxUploadRetryTimeMillis(2000); // Set the maximum retry time for uploads

        StorageReference storageRef = FirebaseStorage.getInstance().getReference(); // Get a reference to the root of the Firebase Storage bucket
        StorageReference profilePicturesRef = storageRef.child("profile_pictures"); // Get a reference to the "profile_pictures" directory

        profilePicturesRef.getMetadata().addOnSuccessListener(metadata -> {
            // The "profile_pictures" directory exists
            uploadImageToProfilePicturesDirectory(imageUri, profilePicturesRef); // Upload the image to the existing directory
        }).addOnFailureListener(exception -> {
            // The "profile_pictures" directory does not exist
            createProfilePicturesDirectory(profilePicturesRef); // Create the "profile_pictures" directory
            uploadImageToProfilePicturesDirectory(imageUri, profilePicturesRef); // Upload the image to the newly created directory
        });
    }

    private void createProfilePicturesDirectory(StorageReference profilePicturesRef) {
        byte[] emptyBytes = new byte[0];
        UploadTask uploadTask = profilePicturesRef.putBytes(emptyBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // The "profile_pictures" directory was created successfully
            Log.d("TeacherHomepageActivity", "Profile pictures directory created");
        }).addOnFailureListener(exception -> {
            // Failed to create the "profile_pictures" directory
            Log.e("TeacherHomepageActivity", "Failed to create profile pictures directory", exception);
        });
    }

    private void uploadImageToProfilePicturesDirectory(Uri imageUri, StorageReference profilePicturesRef) {
        String currentUserUid = currentUser.getUid();
        Log.d("TeacherHomepageActivity", "Current user UID: " + currentUserUid);

        String storagePath = "profile_pictures/" + currentUserUid + ".jpg";
        Log.d("TeacherHomepageActivity", "Storage path: " + storagePath);

        StorageReference imageRef = profilePicturesRef.child(currentUserUid + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            Log.d("TeacherHomepageActivity", "Image upload successful");
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("TeacherHomepageActivity", "Download URL: " + downloadUrl);
                // Store the download URL in your database
                storeProfilePictureUrl(downloadUrl);
            }).addOnFailureListener(exception -> {
                Log.e("TeacherHomepageActivity", "Failed to get download URL", exception);
                Toast.makeText(TeacherHomepageActivity.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(exception -> {
            // Handle upload failure
            Log.e("TeacherHomepageActivity", "Image upload failed", exception);
            Toast.makeText(TeacherHomepageActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Track upload progress
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d("TeacherHomepageActivity", "Upload progress: " + progress + "%");
        });
    }

    private void storeProfilePictureUrl(String downloadUrl) {
        // Store the download URL in your database
        // For example, if you're using Firebase Firestore:
        // FirebaseFirestore.getInstance().collection("users")
        //         .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
        //         .update("profilePictureUrl", downloadUrl)
        //         .addOnSuccessListener(aVoid -> {
        //             // Profile picture URL stored successfully
        //             Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
        //         })
        //         .addOnFailureListener(e -> {
        //             // Handle failure
        //             Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
        //         });
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
