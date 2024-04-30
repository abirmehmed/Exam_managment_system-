package com.example.myapplication.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.models.Exam;

import java.util.List;

public class ExamListAdapter extends RecyclerView.Adapter<ExamListAdapter.ExamViewHolder> {

    private List<Exam> examList;

    public ExamListAdapter(List<Exam> examList) {
        this.examList = examList;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exam, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam exam = examList.get(position);
        holder.tvExamTitle.setText(exam.getTitle());
        holder.tvExamDate.setText(exam.getDate());
        holder.tvExamDuration.setText(String.valueOf(exam.getDuration()) + " minutes");
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView tvExamTitle;
        TextView tvExamDate;
        TextView tvExamDuration;

        ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamTitle = itemView.findViewById(R.id.tv_exam_title);
            tvExamDate = itemView.findViewById(R.id.tv_exam_date);
            tvExamDuration = itemView.findViewById(R.id.tv_exam_duration);
        }
    }
}
