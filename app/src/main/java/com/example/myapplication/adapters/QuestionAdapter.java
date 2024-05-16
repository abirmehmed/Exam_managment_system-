package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionViewHolder> {
    private List<Question> questions;
    private OnQuestionClickListener onQuestionClickListener;
    private OnQuestionEditListener onQuestionEditListener;
    private OnQuestionDeleteListener onQuestionDeleteListener;

    public QuestionAdapter(List<Question> questions) {
        this.questions = questions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
        notifyDataSetChanged();
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
        bind(holder, question, position);

        // Set the OnClickListener for the question item
        holder.itemView.setOnClickListener(v -> {
            if (onQuestionClickListener != null) {
                onQuestionClickListener.onQuestionClick(question);
            }
        });

        // Set the OnClickListener for the edit button
        holder.editButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Question questionToEdit = questions.get(pos);
                if (onQuestionEditListener != null) {
                    onQuestionEditListener.onQuestionEdit(questionToEdit);
                }
            }
        });

        // Set the OnClickListener for the delete button
        holder.deleteButton.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                Question questionToDelete = questions.get(pos);
                if (onQuestionDeleteListener != null) {
                    onQuestionDeleteListener.onQuestionDeleted(questionToDelete);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    public void bind(QuestionViewHolder holder, Question question, int position) {
        holder.questionNumberTextView.setText(String.format("%02d)", position + 1));
        holder.questionTextView.setText(question.getText());

        if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
            // Inflate and populate the options for multiple-choice questions
            holder.optionsLayout.removeAllViews();
            for (int i = 0; i < question.getOptions().size(); i++) {
                TextView optionTextView = new TextView(holder.itemView.getContext());
                optionTextView.setText(String.format("%s) %s", getOptionLetter(i), question.getOptions().get(i)));
                holder.optionsLayout.addView(optionTextView);
            }
            holder.answerEditText.setVisibility(View.VISIBLE);
            holder.answerEditText.setText("Answer: " + getOptionLetter(question.getOptions().indexOf(question.getAnswer())));
            holder.answerEditText.setFocusable(false); // Make the EditText non-editable
        } else if (question.getType() == QuestionType.TRUE_FALSE) {
            // Inflate and populate the options for true/false questions
            holder.optionsLayout.removeAllViews();
            TextView trueOptionTextView = new TextView(holder.itemView.getContext());
            trueOptionTextView.setText("True");
            holder.optionsLayout.addView(trueOptionTextView);

            TextView falseOptionTextView = new TextView(holder.itemView.getContext());
            falseOptionTextView.setText("False");
            holder.optionsLayout.addView(falseOptionTextView);
            holder.answerEditText.setVisibility(View.VISIBLE);
            holder.answerEditText.setText("Answer: " + question.getAnswer());
            holder.answerEditText.setFocusable(false); // Make the EditText non-editable
        } else if (question.getType() == QuestionType.SHORT_ANSWER) {
            // Show the EditText for short answer questions
            holder.optionsLayout.setVisibility(View.GONE);
            holder.answerEditText.setVisibility(View.VISIBLE);
            holder.answerEditText.setHint("Answer");
            holder.answerEditText.setText("");
            holder.answerEditText.setFocusable(true); // Make the EditText editable
        }
    }

    private String getOptionLetter(int index) {
        return String.valueOf((char) ('A' + index));
    }

    public void setOnQuestionClickListener(OnQuestionClickListener listener) {
        this.onQuestionClickListener = listener;
    }

    public void setOnQuestionEditListener(OnQuestionEditListener listener) {
        this.onQuestionEditListener = listener;
    }

    public void setOnQuestionDeleteListener(OnQuestionDeleteListener listener) {
        this.onQuestionDeleteListener = listener;
    }

    public interface OnQuestionClickListener {
        void onQuestionClick(Question question);
    }

    public interface OnQuestionEditListener {
        void onQuestionEdit(Question question);
    }

    public interface OnQuestionDeleteListener {
        void onQuestionDeleted(Question question);
    }
}
