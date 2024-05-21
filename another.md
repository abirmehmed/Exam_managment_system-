Based on the code and data structure you provided, I can see that you have an "exams" collection in Firestore, where each document represents an exam. Inside each exam document, there is a subcollection called "questions" that contains the questions for that exam.

To display the questions in the frontend, you need to make some modifications to your code. Here's how you can update your `ExamActivity` and `ExamQuestionAdapter`:

1. Update the `ExamActivity` to retrieve the questions from the subcollection:

```java
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

        // Start the countdown timer
        startCountdownTimer();
    }

    @Override
    public void onExamDataFetchFailed(Exception e) {
        // Handle the error
    }
});
```

2. Update the `ExamQuestionAdapter` to handle different question types:

```java
class ExamQuestionViewHolder extends RecyclerView.ViewHolder {
    TextView tvQuestionText;
    RadioGroup rgOptions;

    ExamQuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        tvQuestionText = itemView.findViewById(R.id.tv_question_text);
        rgOptions = itemView.findViewById(R.id.rg_options);
    }

    void bind(Question question, int position) {
        tvQuestionText.setText(question.getText());
        rgOptions.removeAllViews();

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
            // For short answer questions, you can't display an input field in the RecyclerView
            // You'll need to handle this case separately in the ExamActivity
            userAnswers.set(position, null);
        }
    }
}
```

In this updated `ExamQuestionAdapter`, we're handling different question types:

- For `MULTIPLE_CHOICE` questions, we're creating `RadioButton` views dynamically and adding them to the `RadioGroup`. The user can select one of the options by clicking on the corresponding `RadioButton`.
- For `TRUE_FALSE` questions, we're creating two `RadioButton` views for "True" and "False" and adding them to the `RadioGroup`. The user can select either "True" or "False" by clicking on the respective `RadioButton`.
- For `SHORT_ANSWER` questions, we're setting the corresponding index in the `userAnswers` list to `null`. You'll need to handle short answer questions separately in the `ExamActivity`.

3. Handle short answer questions in the `ExamActivity`:

```java
// Inside the onExamDataFetched method
List<Question> questions = exam.getQuestions();
examQuestionAdapter = new ExamQuestionAdapter(questions);
rvQuestionList.setLayoutManager(new LinearLayoutManager(ExamActivity.this));
rvQuestionList.setAdapter(examQuestionAdapter);

// Create EditText views for short answer questions
for (int i = 0; i < questions.size(); i++) {
    Question question = questions.get(i);
    if (question.getType() == QuestionType.SHORT_ANSWER) {
        EditText editText = new EditText(ExamActivity.this);
        editText.setHint("Enter your answer");
        shortAnswerEditTexts.add(editText);
        // Add the EditText to your layout (e.g., a LinearLayout or a separate container)
    }
}

// ...

// In the submitExam method
private void submitExam() {
    List<String> userAnswers = examQuestionAdapter.getUserAnswers();

    // Handle short answer questions
    for (int i = 0; i < shortAnswerEditTexts.size(); i++) {
        EditText editText = shortAnswerEditTexts.get(i);
        String shortAnswer = editText.getText().toString();
        // Add the short answer to the userAnswers list or handle it separately
    }

    // Submit the answers to Firebase Firestore or any other data source
    submitAnswersToFirestore(userAnswers);
}
```

In this updated code:

- Inside the `onExamDataFetched` method, we're creating `EditText` views for short answer questions and adding them to the `shortAnswerEditTexts` list. You'll need to add these `EditText` views to your layout (e.g., a `LinearLayout` or a separate container).
- In the `submitExam` method, we're iterating through the `shortAnswerEditTexts` list and retrieving the user's input for each short answer question. You can either add these short answers to the `userAnswers` list or handle them separately.

With these changes, you should be able to display the questions from Firestore in your frontend, including multiple-choice, true/false, and short answer questions. The user can select options for multiple-choice and true/false questions, and enter their answers for short answer questions.

Note: Make sure to update your layout files (`activity_exam.xml` and `item_exam_question.xml`) to accommodate the `EditText` views for short answer questions and ensure proper positioning and layout of the views.
