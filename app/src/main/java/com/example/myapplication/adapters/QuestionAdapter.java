package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final List<Question> questions;
    private OnQuestionClickListener onQuestionClickListener;
    private OnQuestionEditListener onQuestionEditListener;
    private OnQuestionDeleteListener onQuestionDeleteListener;

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
        holder.bind(question, position);

        // Set the OnClickListener for the question item
        holder.itemView.setOnClickListener(v -> {
            if (onQuestionClickListener != null) {
                onQuestionClickListener.onQuestionClick(question);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
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

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private final TextView questionNumberTextView;
        private final TextView questionTextView;
        private final LinearLayout optionsLayout;
        private final EditText answerEditText;
        private final Button editButton;
        private final Button deleteButton;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionNumberTextView = itemView.findViewById(R.id.tv_question_number);
            questionTextView = itemView.findViewById(R.id.tv_question_text);
            optionsLayout = itemView.findViewById(R.id.ll_question_options);
            answerEditText = itemView.findViewById(R.id.et_question_answer);
            editButton = itemView.findViewById(R.id.btn_edit_question);
            deleteButton = itemView.findViewById(R.id.btn_delete_question);

            // Set the OnClickListener for the edit button
            editButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Question questionToEdit = questions.get(position);
                    if (onQuestionEditListener != null) {
                        onQuestionEditListener.onQuestionEdit(questionToEdit);
                    }
                }
            });

            // Set the OnClickListener for the delete button
            deleteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Question questionToDelete = questions.get(position);
                    if (onQuestionDeleteListener != null) {
                        onQuestionDeleteListener.onQuestionDeleted(questionToDelete);
                    }
                }
            });
        }

        public void bind(Question question, int position) {
            questionNumberTextView.setText(String.format("%02d)", position + 1));
            questionTextView.setText(question.getText());

            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                // Inflate and populate the options for multiple-choice questions
                optionsLayout.removeAllViews();
                for (int i = 0; i < question.getOptions().size(); i++) {
                    TextView optionTextView = new TextView(itemView.getContext());
                    optionTextView.setText(String.format("%s) %s", getOptionLetter(i), question.getOptions().get(i)));
                    optionsLayout.addView(optionTextView);
                }
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText("Answer: " + getOptionLetter(question.getOptions().indexOf(question.getAnswer())));
                answerEditText.setFocusable(false); // Make the EditText non-editable
            } else if (question.getType() == QuestionType.TRUE_FALSE) {
                // Inflate and populate the options for true/false questions
                optionsLayout.removeAllViews();
                TextView trueOptionTextView = new TextView(itemView.getContext());
                trueOptionTextView.setText("True");
                optionsLayout.addView(trueOptionTextView);

                TextView falseOptionTextView = new TextView(itemView.getContext());
                falseOptionTextView.setText("False");
                optionsLayout.addView(falseOptionTextView);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setText("Answer: " + question.getAnswer());
                answerEditText.setFocusable(false); // Make the EditText non-editable
            } else if (question.getType() == QuestionType.SHORT_ANSWER) {
                // Show the EditText for short answer questions
                optionsLayout.setVisibility(View.GONE);
                answerEditText.setVisibility(View.VISIBLE);
                answerEditText.setHint("Answer");
                answerEditText.setText("");
                answerEditText.setFocusable(true); // Make the EditText editable
            }
        }

        private String getOptionLetter(int index) {
            return String.valueOf((char) ('A' + index));
        }

    }
}
