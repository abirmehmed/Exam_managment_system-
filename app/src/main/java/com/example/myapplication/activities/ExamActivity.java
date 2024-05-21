package com.example.myapplication.activities;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;
import com.example.myapplication.adapters.ExamQuestionAdapter;
import com.example.myapplication.models.Exam;
import com.example.myapplication.models.Question;
import com.example.myapplication.models.QuestionType;
import com.example.myapplication.managers.ExamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExamActivity extends AppCompatActivity {

        private TextView tvExamName, tvTimer;
        private RecyclerView rvQuestionList;
        private ExamQuestionAdapter examQuestionAdapter;
        private EditText etShortAnswer;
        private LinearLayout llShortAnswerContainer;

        private Button btnSubmitExam;

        private CountDownTimer countDownTimer;
        private long examDurationMillis;
        private ExamRepository examRepository;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_exam);

            tvExamName = findViewById(R.id.tv_exam_name);
            tvTimer = findViewById(R.id.tv_timer);
            rvQuestionList = findViewById(R.id.rv_question_list);
            btnSubmitExam = findViewById(R.id.btn_submit_exam);
            llShortAnswerContainer = findViewById(R.id.ll_short_answer_container);
            etShortAnswer = findViewById(R.id.et_short_answer);

            // Remove etShortAnswer from its parent if it's already attached
            ViewGroup parent = (ViewGroup) etShortAnswer.getParent();
            if (parent != null) {
                parent.removeView(etShortAnswer);
            }

            examRepository = new ExamRepository();
            String examId = getIntent().getStringExtra("examId");

            examRepository.getExamData(examId, new ExamRepository.OnExamDataFetchedListener() {
                @Override
                public void onExamDataFetched(Exam exam) {
                    // Set up the exam details
                    tvExamName.setText(exam.getTitle());
                    examDurationMillis = exam.getDuration() * 60 * 1000;

                    // Set up the RecyclerView and ExamQuestionAdapter
                    List<Question> questions = exam.getQuestions();
                    examQuestionAdapter = new ExamQuestionAdapter(questions);
                    rvQuestionList.setLayoutManager(new LinearLayoutManager(ExamActivity.this));
                    rvQuestionList.setAdapter(examQuestionAdapter);

                    // Check if there is at least one SHORT_ANSWER question
                    boolean hasShortAnswerQuestion = false;
                    for (Question question : questions) {
                        if (question.getType() == QuestionType.SHORT_ANSWER) {
                            hasShortAnswerQuestion = true;
                            break;
                        }
                    }

                    // Remove etShortAnswer from its parent if it's already attached
                    ViewGroup parent = (ViewGroup) etShortAnswer.getParent();
                    if (parent != null) {
                        parent.removeView(etShortAnswer);
                    }

                    // Add the short answer EditText to the container if there is at least one SHORT_ANSWER question
                    if (hasShortAnswerQuestion) {
                        llShortAnswerContainer.addView(etShortAnswer);
                    }

                    // Start the countdown timer
                    startCountdownTimer();
                }

                @Override
                public void onExamDataFetchFailed(Exception e) {
                    // Handle the error
                }
            });

            // Handle the "Submit Exam" button click
            btnSubmitExam.setOnClickListener(v -> submitExam());
        }

        private void submitExam() {
            // Retrieve the user's selected answers from the ExamQuestionAdapter
            List<String> userAnswers = examQuestionAdapter.getUserAnswers();

            // Handle short answer questions
            int shortAnswerIndex = 0;
            for (int i = 0; i < userAnswers.size(); i++) {
                if (userAnswers.get(i) == null) {
                    String shortAnswer = etShortAnswer.getText().toString();
                    userAnswers.set(i, shortAnswer);
                    shortAnswerIndex++;
                }
            }

            // Submit the answers to Firebase Firestore or any other data source
            submitAnswersToFirestore(userAnswers);
        }

        private void startCountdownTimer() {
            countDownTimer = new CountDownTimer(examDurationMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                    tvTimer.setText(String.format("%02d:%02d", minutes, seconds));
                }

                @Override
                public void onFinish() {
                    tvTimer.setText("00:00");
                    // Handle exam timeout
                    submitExam();
                }
            }.start();
        }

        private Exam getExamFromFirestore() {
            // Fetch exam from Firebase Firestore and return an Exam object
            return new Exam();
        }

        private void submitAnswersToFirestore(List<String> userAnswers) {
            // Submit the answers to Firebase Firestore
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // Cancel the countdown timer
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            // Remove the etShortAnswer view from the container
            llShortAnswerContainer.removeView(etShortAnswer);
        }
    }
