package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameEditText = findViewById(R.id.et_username);
        EditText passwordEditText = findViewById(R.id.et_password);
        Button loginButton = findViewById(R.id.btn_login);
        Button registerButton = findViewById(R.id.btn_register);
        RadioGroup roleRadioGroup = findViewById(R.id.rg_role);

        loginButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String password = passwordEditText.getText().toString();
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
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });
    }
}
