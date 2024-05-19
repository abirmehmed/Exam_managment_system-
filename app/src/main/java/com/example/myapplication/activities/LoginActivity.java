package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RadioButton teacherRadioButton;
    private RadioButton studentRadioButton;
    private RadioGroup roleRadioGroup;
    private EditText passwordEditText;
    private ImageView passwordVisibilityIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        EditText emailEditText = findViewById(R.id.et_email);
        passwordEditText = findViewById(R.id.et_password);
        Button loginButton = findViewById(R.id.btn_login);
        Button registerButton = findViewById(R.id.btn_register);
        roleRadioGroup = findViewById(R.id.rg_role);
        teacherRadioButton = findViewById(R.id.rb_teacher);
        studentRadioButton = findViewById(R.id.rb_student);
        passwordVisibilityIcon = findViewById(R.id.iv_password_visibility);

        passwordVisibilityIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isPasswordVisible = passwordEditText.getTransformationMethod() == null;
                passwordEditText.setTransformationMethod(isPasswordVisible ? PasswordTransformationMethod.getInstance() : null);
                passwordVisibilityIcon.setImageResource(isPasswordVisible ? R.drawable.ic_visibility_off : R.drawable.ic_visibility);
            }
        });
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                emailEditText.setError("Email is required.");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                passwordEditText.setError("Password is required.");
                return;
            }

            // Sign in the user with email and password
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Sign-in successful, handle the user's role
                            FirebaseUser user = mAuth.getCurrentUser();
                            handleUserRole(user);
                        } else {
                            // Sign-in failed, display an error message
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        registerButton.setOnClickListener(v -> {
            // Navigate to the RegistrationActivity
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void handleUserRole(FirebaseUser user) {
        if (user != null) {
            Log.d("LoginActivity", "Current user: " + user.getUid());
            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
            if (selectedRoleId == R.id.rb_teacher) {
                // Proceed to TeacherHomepageActivity
                Intent intent = new Intent(this, TeacherHomepageActivity.class);
                startActivity(intent);
            } else if (selectedRoleId == R.id.rb_student) {
                // Proceed to StudentHomepageActivity
                Intent intent = new Intent(this, StudentHomepageActivity.class);
                startActivity(intent);
            } else {
                // No radio button selected
                Toast.makeText(this, "Please select a user role.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("LoginActivity", "Current user: null");
            // Handle the case where the user is not authenticated
            // For example, you can show an error message or redirect the user to the login screen
            Toast.makeText(this, "User is not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }
}