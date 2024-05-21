To display question numbers in front of each question, you can modify the `ExamQuestionAdapter` and the `ExamQuestionViewHolder` class. Here's how you can update the code:

1. In the `ExamQuestionAdapter` class, add a new variable to keep track of the question number:

```java
public class ExamQuestionAdapter extends RecyclerView.Adapter<ExamQuestionAdapter.ExamQuestionViewHolder> {
    private List<Question> questions;
    private List<String> userAnswers;
    private int questionNumber = 1; // Initialize the question number to 1

    // ... (rest of the code)
}
```

2. In the `onBindViewHolder` method of the `ExamQuestionAdapter` class, pass the `questionNumber` to the `bind` method of the `ExamQuestionViewHolder`:

```java
@Override
public void onBindViewHolder(@NonNull ExamQuestionViewHolder holder, int position) {
    Question question = questions.get(position);
    holder.bind(question, position, questionNumber++); // Pass the questionNumber and increment it
}
```

3. In the `ExamQuestionViewHolder` class, update the `bind` method to accept the `questionNumber` and display it in front of the question text:

```java
class ExamQuestionViewHolder extends RecyclerView.ViewHolder {
    TextView tvQuestionText;
    RadioGroup rgOptions;

    ExamQuestionViewHolder(@NonNull View itemView) {
        super(itemView);
        tvQuestionText = itemView.findViewById(R.id.tv_question_text);
        rgOptions = itemView.findViewById(R.id.rg_options);
    }

    void bind(Question question, int position, int questionNumber) {
        tvQuestionText.setText(questionNumber + ". " + question.getText());
        rgOptions.removeAllViews();

        // ... (rest of the code)
    }
}
```

In the updated `bind` method, we're concatenating the `questionNumber` with the question text using `questionNumber + ". " + question.getText()` and setting it to the `tvQuestionText`.

After making these changes, your `ExamQuestionAdapter` and `ExamQuestionViewHolder` classes should look like this:

```java
public class ExamQuestionAdapter extends RecyclerView.Adapter<ExamQuestionAdapter.ExamQuestionViewHolder> {
    private List<Question> questions;
    private List<String> userAnswers;
    private int questionNumber = 1; // Initialize the question number to 1

    // ... (constructor and other methods)

    @NonNull
    @Override
    public ExamQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_question, parent, false);
        return new ExamQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamQuestionViewHolder holder, int position) {
        Question question = questions.get(position);
        holder.bind(question, position, questionNumber++); // Pass the questionNumber and increment it
    }

    // ... (rest of the code)

    class ExamQuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        RadioGroup rgOptions;

        ExamQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tv_question_text);
            rgOptions = itemView.findViewById(R.id.rg_options);
        }

        void bind(Question question, int position, int questionNumber) {
            tvQuestionText.setText(questionNumber + ". " + question.getText());
            rgOptions.removeAllViews();

            // ... (rest of the code)
        }
    }
}
```

With these changes, each question in the `RecyclerView` should be displayed with a question number in front of it, like:

```
1. Question text
2. Question text
3. Question text
```

Make sure to update the `item_exam_question.xml` layout file if needed, to accommodate the longer question text with the question number.
