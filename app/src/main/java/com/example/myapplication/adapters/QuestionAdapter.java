package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final List<Question> questions;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionTextView;
        private final LinearLayout optionsLayout;
        private final EditText answerEditText;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.tv_question_text);
            optionsLayout = itemView.findViewById(R.id.ll_question_options);
            answerEditText = itemView.findViewById(R.id.et_question_answer);
        }

        public void bind(Question question) {
            questionTextView.setText(question.getText());

            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                // Inflate and populate the options for multiple-choice questions
                optionsLayout.removeAllViews();
                for (String option : question.getOptions()) {
                    RadioButton radioButton = (RadioButton) LayoutInflater.from(itemView.getContext())
                            .inflate(R.layout.item_option, optionsLayout, false);
                    radioButton.setText(option);
                    optionsLayout.addView(radioButton);
                }
            } else if (question.getType() == QuestionType.TRUE_FALSE) {
                // Inflate and populate the options for true/false questions
                optionsLayout.removeAllViews();
                RadioButton trueOption = (RadioButton) LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_option, optionsLayout, false);
                trueOption.setText(R.string.true_option);
                optionsLayout.addView(trueOption);

                RadioButton falseOption = (RadioButton) LayoutInflater.from(itemView.getContext())
                        .inflate(R.layout.item_option, optionsLayout, false);
                falseOption.setText(R.string.false_option);
                optionsLayout.addView(falseOption);
            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                // Show the EditText for short answer questions
                optionsLayout.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
            }
        }
    }
}
