The issue you're facing is likely due to the way you're navigating between activities. In Android, you need to start a new activity explicitly by creating an `Intent` and calling the `startActivity` method.

To navigate from the `ExamListActivity` (where you have the list of exams) to the `ExamActivity` (where you display the exam details and questions), you need to follow these steps:

1. In the `ExamListActivity`, set up a click listener for the `RecyclerView` items (exams).

2. Inside the click listener, create an `Intent` to start the `ExamActivity` and pass the necessary data (e.g., exam ID) as extras.

3. Call the `startActivity` method with the created `Intent`.

Here's an example of how you can implement this in the `ExamListActivity`:

```java
// Assuming you have an ExamListAdapter that extends RecyclerView.Adapter
private ExamListAdapter examListAdapter;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exam_list);

    RecyclerView rvExamList = findViewById(R.id.rv_exam_list);
    examListAdapter = new ExamListAdapter(examList);
    rvExamList.setAdapter(examListAdapter);

    // Set up the click listener for the exam items
    examListAdapter.setOnExamClickListener(exam -> {
        Intent intent = new Intent(ExamListActivity.this, ExamActivity.class);
        intent.putExtra("examId", exam.getId());
        startActivity(intent);
    });
}
```

In the `ExamListAdapter`, you'll need to define an interface for the click listener and implement it in the `ExamListActivity`. Here's an example:

```java
// In the ExamListAdapter class
public interface OnExamClickListener {
    void onExamClick(Exam exam);
}

private OnExamClickListener onExamClickListener;

public void setOnExamClickListener(OnExamClickListener listener) {
    this.onExamClickListener = listener;
}

// In the onBindViewHolder method
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
```

In the `ExamActivity`, you'll need to retrieve the exam ID from the `Intent` extras and use it to fetch the exam data from Firebase Firestore or any other data source.

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exam);

    // ... (other initialization code)

    String examId = getIntent().getStringExtra("examId");
    if (examId != null) {
        examRepository.getExamData(examId, new OnExamDataFetchedListener() {
            // ... (implementation of onExamDataFetched and onExamDataFetchFailed)
        });
    }
}
```

By following these steps, you should be able to navigate from the `ExamListActivity` to the `ExamActivity` when clicking on an exam item in the list. The `ExamActivity` will receive the exam ID and fetch the exam data accordingly.
