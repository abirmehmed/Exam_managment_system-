package com.example.myapplication.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import java.util.ArrayList;
import java.util.List;

public class AddQuestionDialog extends DialogFragment {
    private EditText etQuestionText;
    private Spinner spinnerQuestionType;
    private LinearLayout llQuestionOptions;
    private EditText etAnswer;
    private OnQuestionAddedListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_add_question, null);

        etQuestionText = view.findViewById(R.id.et_question_text);
        spinnerQuestionType = view.findViewById(R.id.spinner_question_type);
        llQuestionOptions = view.findViewById(R.id.ll_question_options);
        etAnswer = view.findViewById(R.id.et_answer);

        spinnerQuestionType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateQuestionTypeUI(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view)
                .setPositiveButton("Add", (dialog, which) -> {
                    String questionText = etQuestionText.getText().toString().trim();
                    QuestionType questionType = QuestionType.values()[spinnerQuestionType.getSelectedItemPosition()];
                    List<String> options = getQuestionOptions();
                    String answer = etAnswer.getText().toString().trim();

                    Question question = new Question(questionText, questionType, options, answer);
                    if (listener != null) {
                        listener.onQuestionAdded(question);
                    }
                })
                .setNegativeButton("Cancel", null);

        return builder.create();
    }

    private void updateQuestionTypeUI(int position) {
        QuestionType questionType = QuestionType.values()[position];
        switch (questionType) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                llQuestionOptions.setVisibility(View.VISIBLE);
                etAnswer.setVisibility(View.GONE);
                break;
            case SHORT_ANSWER:
                llQuestionOptions.setVisibility(View.GONE);
                etAnswer.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setListener(OnQuestionAddedListener listener) {
        this.listener = listener;
    }

    public interface OnQuestionAddedListener {
        void onQuestionAdded(Question question);
    }

    private List<String> getQuestionOptions() {
        List<String> options = new ArrayList<>();
        if (llQuestionOptions.getVisibility() == View.VISIBLE) {
            for (int i = 0; i < llQuestionOptions.getChildCount(); i++) {
                View view = llQuestionOptions.getChildAt(i);
                if (view instanceof EditText) {
                    String option = ((EditText) view).getText().toString().trim();
                    if (!option.isEmpty()) {
                        options.add(option);
                    }
                }
            }
        }
        return options;
    }
}
