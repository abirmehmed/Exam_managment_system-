To display the questions from Firestore in the `ExamActivity` and show clickable round boxes for multiple-choice questions, you'll need to update the `QuestionAdapter` and the layout file for the question items.

First, let's update the `QuestionAdapter` to handle different question types and display the options with clickable round boxes.

1. Create a new layout file for the question item, e.g., `item_question.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tv_question_text"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="16sp" />

    <RadioGroup
        android:id="@+id/rg_options"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical" />

</LinearLayout>
```

2. Update the `QuestionAdapter` to bind the question data and handle different question types:

```java
public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private List<Question> questions;

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

    class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        RadioGroup rgOptions;

        QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            rgOptions = itemView.findViewById(R.id.rg_options);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getText());
            rgOptions.removeAllViews();

            if (question.getType() == QuestionType.MULTIPLE_CHOICE) {
                for (int i = 0; i < question.getOptions().size(); i++) {
                    RadioButton radioButton = new RadioButton(itemView.getContext());
                    radioButton.setText(question.getOptions().get(i));
                    rgOptions.addView(radioButton);
                }
            } else if (question.getType() == QuestionType.TRUE_FALSE) {
                RadioButton trueButton = new RadioButton(itemView.getContext());
                trueButton.setText("True");
                rgOptions.addView(trueButton);

                RadioButton falseButton = new RadioButton(itemView.getContext());
                falseButton.setText("False");
                rgOptions.addView(falseButton);
            }
        }
    }
}
```

In this updated `QuestionAdapter`, we're using a `RadioGroup` to display the options for multiple-choice and true/false questions. The `bind` method checks the question type and adds the corresponding `RadioButton` views to the `RadioGroup`.

3. In the `ExamActivity`, update the `QuestionAdapter` initialization:

```java
List<Question> questions = exam.getQuestions();
questionAdapter = new QuestionAdapter(questions);
rvQuestionList.setLayoutManager(new LinearLayoutManager(ExamActivity.this));
rvQuestionList.setAdapter(questionAdapter);
```

With these changes, the `ExamActivity` should display the list of questions from Firestore, and the `QuestionAdapter` will render the questions with clickable round boxes (represented by `RadioButton` views) for multiple-choice and true/false questions.

Note: Make sure to update the `Question` and `QuestionType` models to match the structure of the data in Firestore, and handle any additional fields or properties as needed.
