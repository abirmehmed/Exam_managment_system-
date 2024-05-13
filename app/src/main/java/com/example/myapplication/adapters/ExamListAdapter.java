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
    private OnExamClickListener onExamClickListener;

    public ExamListAdapter(List<Exam> examList) {
        this.examList = examList;
    }

    public void setOnExamClickListener(OnExamClickListener listener) {
        this.onExamClickListener = listener;
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
        holder.bind(exam);
        holder.itemView.setOnClickListener(v -> {
            if (onExamClickListener != null) {
                onExamClickListener.onExamClick(exam);
            }
        });
    }

    @Override
    public int getItemCount() {
        return examList.size();
    }

    public interface OnExamClickListener {
        void onExamClick(Exam exam);
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

        void bind(Exam exam) {
            tvExamTitle.setText(exam.getTitle());
            tvExamDate.setText(exam.getDate());
            tvExamDuration.setText(String.valueOf(exam.getDuration()) + " minutes");
        }
    }
}
