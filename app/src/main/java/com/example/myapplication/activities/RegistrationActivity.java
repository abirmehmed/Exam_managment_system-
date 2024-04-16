package com.example.myapplication.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;

import java.util.Calendar;

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
        RadioGroup userTypeRadioGroup = findViewById(R.id.rg_user_type);
        Button registerButton = findViewById(R.id.btn_register);

        dateOfBirthEditText.setOnClickListener(v -> showDatePickerDialog(dateOfBirthEditText));

        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            String dateOfBirth = dateOfBirthEditText.getText().toString();

            int selectedUserTypeId = userTypeRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedUserTypeRadioButton = findViewById(selectedUserTypeId);
            String userType = selectedUserTypeRadioButton.getText().toString();

            // Perform registration logic here
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
}
