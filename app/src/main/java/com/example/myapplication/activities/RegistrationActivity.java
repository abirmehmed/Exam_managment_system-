package com.example.myapplication.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class RegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        EditText usernameEditText = findViewById(R.id.et_username);
        EditText emailEditText = findViewById(R.id.et_email);
        EditText passwordEditText = findViewById(R.id.et_password);
        EditText confirmPasswordEditText = findViewById(R.id.et_confirm_password);
        EditText dateOfBirthEditText = findViewById(R.id.et_date_of_birth);
        Button registerButton = findViewById(R.id.btn_register);

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String dateOfBirth = dateOfBirthEditText.getText().toString();
            // Perform registration logic here
        });
    }
}
