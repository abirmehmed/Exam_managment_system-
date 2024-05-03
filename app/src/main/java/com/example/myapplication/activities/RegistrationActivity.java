package com.example.myapplication.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.example.myapplication.models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class RegistrationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        EditText usernameEditText = findViewById(R.id.et_username);
        EditText emailEditText = findViewById(R.id.et_email);
        EditText passwordEditText = findViewById(R.id.et_password);
        EditText confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        EditText dateOfBirthEditText = findViewById(R.id.et_date_of_birth);
        RadioGroup userTypeRadioGroup = findViewById(R.id.rg_user_type);
        Button registerButton = findViewById(R.id.btn_register);

        dateOfBirthEditText.setOnClickListener(v -> showDatePickerDialog(dateOfBirthEditText));

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            String dateOfBirth = dateOfBirthEditText.getText().toString().trim();

            int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedUserTypeRadioButton = findViewById(selectedUserTypeId);
            String userType = selectedUserTypeRadioButton.getText().toString();

            // Validate the input fields
            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(dateOfBirth)) {
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
                            saveUserData(user, username, dateOfBirth, userType);

                            // Proceed to the appropriate homepage based on user type
                            Intent intent;
                            if (userType.equals("Teacher")) {
                                intent = new Intent(RegistrationActivity.this, TeacherHomepageActivity.class);
                            } else {
                                intent = new Intent(RegistrationActivity.this, StudentHomepageActivity.class);
                            }
                            startActivity(intent);
                            clearInputFields(usernameEditText, emailEditText, passwordEditText, confirmPasswordEditText, dateOfBirthEditText);
                        } else {
                            // Registration failed, display an error message
                            Toast.makeText(RegistrationActivity.this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void showDatePickerDialog(EditText dateOfBirthEditText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateOfBirthEditText.setText(selectedDate);
                },
                year,
                month,
                day
        );
        datePickerDialog.show();
    }

    private void saveUserData(FirebaseUser user, String username, String dateOfBirth, String userType) {
        // Create a user data object
        UserData userData = new UserData(username, dateOfBirth, userType);

        // Save the user data to Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
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
}
