package com.example.myapplication.adapters;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

public class QuestionViewHolder extends RecyclerView.ViewHolder {
    public final TextView questionNumberTextView;
    public final TextView questionTextView;
    public final LinearLayout optionsLayout;
    public final EditText answerEditText;
    public final Button editButton;
    public final Button deleteButton;

    public QuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        questionNumberTextView = itemView.findViewById(R.id.tv_question_number);
        questionTextView = itemView.findViewById(R.id.tv_question_text);
        optionsLayout = itemView.findViewById(R.id.ll_question_options);
        answerEditText = itemView.findViewById(R.id.et_question_answer);
        editButton = itemView.findViewById(R.id.btn_edit_question);
        deleteButton = itemView.findViewById(R.id.btn_delete_question);
    }
}
