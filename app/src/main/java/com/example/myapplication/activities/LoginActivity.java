package com.example.myapplication.activities;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RadioGroup roleRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        EditText usernameEditText = findViewById(R.id.et_username);
        EditText passwordEditText = findViewById(R.id.et_password);
        Button loginButton = findViewById(R.id.btn_login);
        Button registerButton = findViewById(R.id.btn_register);
        roleRadioGroup = findViewById(R.id.rg_role);

        loginButton.setOnClickListener(v -> {
            String email = usernameEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                // Handle empty email
                return;
            }

            if (TextUtils.isEmpty(password)) {
                // Handle empty password
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
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    private void handleUserRole(FirebaseUser user) {
        int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedRoleRadioButton = findViewById(selectedRoleId);

        if (selectedRoleRadioButton.getId() == R.id.rb_teacher) {
            // Proceed to TeacherHomepageActivity
            Intent intent = new Intent(this, TeacherHomepageActivity.class);
            startActivity(intent);
        } else if (selectedRoleRadioButton.getId() == R.id.rb_student) {
            // Proceed to StudentHomepageActivity
            Intent intent = new Intent(this, StudentHomepageActivity.class);
            startActivity(intent);
        } else {
            // Handle other login scenarios
        }
    }
}
