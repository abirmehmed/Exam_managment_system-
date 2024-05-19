package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText passwordEditText, confirmPasswordEditText;
    private ImageView passwordVisibilityIcon, confirmPasswordVisibilityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Authentication and Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EditText usernameEditText = findViewById(R.id.et_username);
        EditText emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        Button registerButton = findViewById(R.id.btn_register);
        passwordVisibilityIcon = findViewById(R.id.iv_password_visibility);
        confirmPasswordVisibilityIcon = findViewById(R.id.iv_confirm_password_visibility);

        passwordVisibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(passwordEditText, passwordVisibilityIcon);
            }
        });

        confirmPasswordVisibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(confirmPasswordEditText, confirmPasswordVisibilityIcon);
            }
        });

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();

            // Validate the input fields
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                // Handle empty fields
                Toast.makeText(RegistrationActivity.this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                // Handle password mismatch
                Toast.makeText(RegistrationActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create a new user with email and password
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Registration successful, save user data to Firestore
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // Save user data to Firestore
                                saveUserData(user, username);

                                // Clear the input fields
                                clearInputFields(usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText);

                                // Navigate to the LoginActivity
                                startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                                finish(); // Optional: finish the RegistrationActivity to prevent going back to it
                            }
                        } else {
                            // Registration failed, display an error message
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            Toast.makeText(RegistrationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void saveUserData(FirebaseUser user, String username) {
        // Create a user data object
        UserData userData = new UserData(username, ""); // No user type needed

        // Save the user data to Firestore
        db.collection("users")
                .document(user.getUid())
                .set(userData)
                .addOnSuccessListener(aVoid -> {
                    // User data saved successfully
                })
                .addOnFailureListener(e -> {
                    // Handle error saving user data
                    Toast.makeText(RegistrationActivity.this, "Failed to save user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void clearInputFields(EditText... editTexts) {
        for (EditText editText : editTexts) {
            editText.setText("");
        }
    }

    private void togglePasswordVisibility(EditText passwordEditText, ImageView passwordVisibilityIcon) {
        boolean isPasswordVisible = passwordEditText.getTransformationMethod() == null;
        passwordEditText.setTransformationMethod(isPasswordVisible ? PasswordTransformationMethod.getInstance() : null);
        passwordVisibilityIcon.setImageResource(isPasswordVisible ? R.drawable.ic_visibility_off : R.drawable.ic_visibility);
    }
}