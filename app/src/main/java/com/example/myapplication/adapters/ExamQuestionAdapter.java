package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.widget.EditText;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.Editable;

public class ExamQuestionAdapter extends RecyclerView.Adapter<ExamQuestionAdapter.ExamQuestionViewHolder> {
    private List<Question> questions;
    private List<String> userAnswers;
    private int questionNumber = 1; // Initialize the question number to 1

    public ExamQuestionAdapter(List<Question> questions) {
        this.questions = questions;
        this.userAnswers = new ArrayList<>(Collections.nCopies(questions.size(), null));
    }

    public List<String> getUserAnswers() {
        return userAnswers;
    }

    @NonNull
    @Override
    public ExamQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_question, parent, false);
        return new ExamQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamQuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position, questionNumber++);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    class ExamQuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        RadioGroup rgOptions;
        EditText etShortAnswer;

        ExamQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            rgOptions = itemView.findViewById(R.id.rg_options);
            etShortAnswer = itemView.findViewById(R.id.et_short_answer);
        }

        void bind(Question question, int position, int questionNumber) {
            tvQuestionText.setText(questionNumber + ". " + question.getText());
            rgOptions.removeAllViews();
            etShortAnswer.setVisibility(View.GONE);

            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                for (int i = 0; i < question.getOptions().size(); i++) {
                    RadioButton radioButton = new RadioButton(itemView.getContext());
                    radioButton.setText(question.getOptions().get(i));
                    radioButton.setTag(i); // Set the option index as the tag
                    rgOptions.addView(radioButton);

                    // Check if the user has selected this option
                    if (userAnswers.get(position) != null && userAnswers.get(position).equals(question.getOptions().get(i))) {
                        radioButton.setChecked(true);
                    }

                    // Set the click listener for the radio button
                    radioButton.setOnClickListener(v -> {
                        int selectedIndex = (int) v.getTag();
                        userAnswers.set(position, question.getOptions().get(selectedIndex));
                    });
                }
            } else if (question.getType() == QuestionType.TRUE_FALSE) {
                RadioButton trueButton = new RadioButton(itemView.getContext());
                trueButton.setText("True");
                trueButton.setTag(0); // Set the tag as 0 for True
                rgOptions.addView(trueButton);

                RadioButton falseButton = new RadioButton(itemView.getContext());
                falseButton.setText("False");
                falseButton.setTag(1); // Set the tag as 1 for False
                rgOptions.addView(falseButton);

                // Check if the user has selected an option
                if (userAnswers.get(position) != null) {
                    if (userAnswers.get(position).equals("True")) {
                        trueButton.setChecked(true);
                    } else if (userAnswers.get(position).equals("False")) {
                        falseButton.setChecked(true);
                    }
                }

                // Set the click listener for the radio buttons
                trueButton.setOnClickListener(v -> userAnswers.set(position, "True"));
                falseButton.setOnClickListener(v -> userAnswers.set(position, "False"));
            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                rgOptions.setVisibility(View.GONE);
                etShortAnswer.setVisibility(View.VISIBLE);

                // Set the user's answer if available
                if (userAnswers.get(position) != null) {
                    etShortAnswer.setText(userAnswers.get(position));
                }

                // Set the text change listener for the EditText
                etShortAnswer.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // Not needed
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // Not needed
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        userAnswers.set(position, s.toString());
                    }
                });
            }
        }
    }
}
