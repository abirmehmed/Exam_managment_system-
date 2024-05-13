The errors you're facing are due to the following reasons:

1. `Cannot resolve symbol 'etExamName'`, `Cannot resolve symbol 'etExamDate'`, and `Cannot resolve symbol 'etExamDuration'`: These errors occur because the `etExamName`, `etExamDate`, and `etExamDuration` variables are not defined in your `ExamEditorActivity` class. You need to declare and initialize these variables as `EditText` views in your activity layout file and then find them using `findViewById` in your activity code.

2. `Cannot resolve symbol 'Exam'` and `Cannot resolve symbol 'Exam'`: These errors occur because the `Exam` class is not defined or imported in your `ExamEditorActivity` class. You need to create a separate Java class called `Exam` to represent the exam data model, and then import it in your `ExamEditorActivity` class.

3. `'add(java.lang.Object)' in 'com.google.firebase.firestore.CollectionReference' cannot be applied to '(Exam)'`: This error occurs because the `add` method in the `CollectionReference` class expects a `Map<String, Object>` object, but you're trying to pass an `Exam` object directly. You need to convert your `Exam` object into a `Map<String, Object>` before adding it to the Firestore collection.

Here's how you can resolve these issues:

1. In your activity layout file (e.g., `activity_exam_editor.xml`), add `EditText` views for the exam name, date, and duration, and assign them appropriate IDs (e.g., `et_exam_name`, `et_exam_date`, `et_exam_duration`).

2. In your `ExamEditorActivity` class, declare and initialize the `EditText` variables using `findViewById`:

```java
private EditText etExamName;
private EditText etExamDate;
private EditText etExamDuration;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exam_editor);

    etExamName = findViewById(R.id.et_exam_name);
    etExamDate = findViewById(R.id.et_exam_date);
    etExamDuration = findViewById(R.id.et_exam_duration);

    // ...
}
```

3. Create a new Java class called `Exam` to represent the exam data model. This class should have fields for the exam name, date, duration, and a list of questions. You can also include a constructor and getter/setter methods as needed.

```java
public class Exam {
    private String examName;
    private String examDate;
    private String examDuration;
    private List<Question> questions;

    // Constructor, getters, and setters
}
```

4. In your `saveExamToFirestore` method, convert the `Exam` object into a `Map<String, Object>` before adding it to the Firestore collection:

```java
private void saveExamToFirestore() {
    // Get the exam data from the UI
    String examName = etExamName.getText().toString().trim();
    String examDate = etExamDate.getText().toString().trim();
    String examDuration = etExamDuration.getText().toString().trim();

    // Create a new Exam object
    Exam exam = new Exam(examName, examDate, examDuration, questions);

    // Convert the Exam object to a Map<String, Object>
    Map<String, Object> examData = new HashMap<>();
    examData.put("examName", exam.getExamName());
    examData.put("examDate", exam.getExamDate());
    examData.put("examDuration", exam.getExamDuration());

    // Save the exam data to Firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    db.collection("exams")
        .add(examData)
        .addOnSuccessListener(documentReference -> {
            // Exam document created successfully
            String examId = documentReference.getId();

            // Save the questions as subcollections
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                db.collection("exams")
                    .document(examId)
                    .collection("questions")
                    .add(question)
                    .addOnSuccessListener(questionDocRef -> {
                        // Question document created successfully
                    })
                    .addOnFailureListener(e -> {
                        // Error creating question document
                    });
            }
        })
        .addOnFailureListener(e -> {
            // Error creating exam document
        });
}
```

In this updated code:

- The `EditText` variables (`etExamName`, `etExamDate`, `etExamDuration`) are declared and initialized in the `onCreate` method.
- The `Exam` class is created as a separate Java class, and it is imported in the `ExamEditorActivity` class.
- The `Exam` object is converted to a `Map<String, Object>` using a `HashMap`, and this `Map` is added to the Firestore collection using the `add` method.

After making these changes, the errors should be resolved, and you should be able to save the exam data to Firestore correctly.

Note: Make sure to import the necessary classes and packages, such as `java.util.HashMap`, `java.util.Map`, and `com.google.firebase.firestore.FirebaseFirestore`, in your `ExamEditorActivity` class.
