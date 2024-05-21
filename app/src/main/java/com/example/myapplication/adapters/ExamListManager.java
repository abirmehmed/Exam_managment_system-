package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;

import com.example.myapplication.activities.ExamActivity;
import com.example.myapplication.adapters.ExamListAdapter;
import com.example.myapplication.models.Exam;

import java.util.List;

public class ExamListManager {
    private Context context;
    private ExamListAdapter examListAdapter;

    public ExamListManager(Context context, List<Exam> examList) {
        this.context = context;
        this.examListAdapter = new ExamListAdapter(examList);
        setupExamClickListener();
    }

    private void setupExamClickListener() {
        examListAdapter.setOnExamClickListener(exam -> {
            Intent intent = new Intent(context, ExamActivity.class);
            intent.putExtra("examId", exam.getId());
            context.startActivity(intent);
        });
    }

    public ExamListAdapter getExamListAdapter() {
        return examListAdapter;
    }
}
