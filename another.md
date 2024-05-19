Based on the code you provided, it seems that you are already passing the exam title, date, duration, and ID from the `ExamManagementActivity` to the `ExamEditorActivity` when creating a new exam or editing an existing one.

However, if you want to display the exam title in the `ExamEditorActivity` from the data retrieved from Firebase, you can follow these steps:

1. In the `ExamEditorActivity`, remove the line where you retrieve the exam title from the intent extras:

```java
// String examTitle = getIntent().getStringExtra("examTitle");
```

2. Instead, retrieve the exam document ID from the intent extras and use it to fetch the exam details from Firebase:

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exam_editor);

    // Retrieve the exam document ID from the Intent extras
    String examId = getIntent().getStringExtra("examId");

    // Fetch the exam details from Firebase using the document ID
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("exams")
        .document(examId)
        .get()
        .addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String examTitle = documentSnapshot.getString("title");
                String examDate = documentSnapshot.getString("date");
                int examDuration = documentSnapshot.getLong("duration").intValue();

                // Initialize the ExamManager and QuestionManager with the retrieved data
                List<Question> questions = new ArrayList<>();
                examManager = new ExamManager(examId, examTitle, examDate, examDuration, questions);
                questionManager = new QuestionManager(questions);

                // Set the exam title to the TextView
                TextView tvExamTitle = findViewById(R.id.tv_exam_title);
                tvExamTitle.setText(examTitle);

                // ... (other initialization code)
            } else {
                // Handle the case where the document doesn't exist
            }
        })
        .addOnFailureListener(e -> {
            // Handle failure
        });
}
```

In this updated code, we retrieve the exam document ID from the intent extras and use it to fetch the exam details from Firebase. Once we have the exam title, date, and duration, we initialize the `ExamManager` and `QuestionManager` with the retrieved data, and we set the exam title to the `TextView` with the ID `tv_exam_title`.

3. In the `ExamManagementActivity`, when launching the `ExamEditorActivity`, pass only the exam document ID as an extra in the intent:

```java
// In the onExamClick method
@Override
public void onExamClick(Exam exam) {
    // Launch the ExamEditorActivity and pass the exam data
    Intent intent = new Intent(this, ExamEditorActivity.class);
    intent.putExtra("examId", exam.getId());
    startActivity(intent);
}

// In the createAndLaunchExamEditor method
private void createAndLaunchExamEditor(String examTitle, String examDate, int examDuration) {
    // Create a new exam document in Firestore
    Map<String, Object> examData = new HashMap<>();
    examData.put("title", examTitle);
    examData.put("date", examDate);
    examData.put("duration", examDuration);

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("exams")
            .add(examData)
            .addOnSuccessListener(documentReference -> {
                // Exam document created successfully
                String examId = documentReference.getId();

                // Pass the exam ID to the ExamEditorActivity
                Intent intent = new Intent(ExamManagementActivity.this, ExamEditorActivity.class);
                intent.putExtra("examId", examId);
                startActivity(intent);
            })
            .addOnFailureListener(e -> {
                // Error creating exam document
                Toast.makeText(ExamManagementActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
}
```

In these updated methods, we only pass the exam document ID as an extra in the intent when launching the `ExamEditorActivity`.

By following these steps, the `ExamEditorActivity` will retrieve the exam title and other details directly from Firebase using the exam document ID, and display the exam title in the `TextView` with the ID `tv_exam_title`.
