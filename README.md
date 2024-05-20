To retrieve the exam data from Firebase Firestore and display the questions without the answers in the `ExamActivity`, you can follow these steps:

1. Create a new class called `ExamRepository` or `FirestoreManager` (or any other name you prefer) to handle the interaction with Firebase Firestore.

2. In the `ExamRepository` class, create a method to fetch the exam data from Firestore. Here's an example implementation:

```java
public class ExamRepository {
    private static final String EXAMS_COLLECTION = "exams";
    private static final String QUESTIONS_COLLECTION = "questions";

    private FirebaseFirestore firestore;

    public ExamRepository() {
        firestore = FirebaseFirestore.getInstance();
    }

    public void getExamData(String examId, OnExamDataFetchedListener listener) {
        firestore.collection(EXAMS_COLLECTION)
                .document(examId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Exam exam = documentSnapshot.toObject(Exam.class);
                        exam.setId(documentSnapshot.getId());

                        firestore.collection(EXAMS_COLLECTION)
                                .document(examId)
                                .collection(QUESTIONS_COLLECTION)
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    List<Question> questions = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : querySnapshot) {
                                        Question question = document.toObject(Question.class);
                                        question.setDocumentId(document.getId());
                                        questions.add(question);
                                    }
                                    exam.setQuestions(questions);
                                    listener.onExamDataFetched(exam);
                                })
                                .addOnFailureListener(e -> listener.onExamDataFetchFailed(e));
                    } else {
                        listener.onExamDataFetchFailed(new Exception("Exam not found"));
                    }
                })
                .addOnFailureListener(e -> listener.onExamDataFetchFailed(e));
    }

    public interface OnExamDataFetchedListener {
        void onExamDataFetched(Exam exam);
        void onExamDataFetchFailed(Exception e);
    }
}
```

3. In the `ExamActivity`, create an instance of the `ExamRepository` and call the `getExamData` method to fetch the exam data.

```java
private ExamRepository examRepository;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_exam);

    // ... (other initialization code)

    examRepository = new ExamRepository();
    String examId = "ea9gXqquWPBMhUXGPpPK"; // Replace with the actual exam ID
    examRepository.getExamData(examId, new ExamRepository.OnExamDataFetchedListener() {
        @Override
        public void onExamDataFetched(Exam exam) {
            // Set up the exam details
            tvExamName.setText(exam.getTitle());
            examDurationMillis = exam.getDuration() * 60 * 1000;

            // Set up the RecyclerView and QuestionAdapter
            List<Question> questions = exam.getQuestions();
            questionAdapter = new QuestionAdapter(questions);
            rvQuestionList.setLayoutManager(new LinearLayoutManager(ExamActivity.this));
            rvQuestionList.setAdapter(questionAdapter);

            // Start the countdown timer
            startCountdownTimer();
        }

        @Override
        public void onExamDataFetchFailed(Exception e) {
            // Handle the error
        }
    });
}
```

4. In the `QuestionAdapter`, modify the `bind` method to hide the answer for each question. You can remove the code that sets the answer or make it conditional based on whether the exam has been submitted or not.

```java
public void bind(QuestionViewHolder holder, Question question, int position) {
    // ... (other code)

    // Hide the answer
    holder.answerEditText.setVisibility(View.GONE);
}
```

With these changes, when you click on an exam from the list, the `ExamActivity` will fetch the exam data from Firebase Firestore, including the questions. The questions will be displayed in the `RecyclerView` without showing the answers. When the user submits the exam, you can retrieve the answers from the `QuestionAdapter` and submit them to Firebase Firestore or any other data source.

Note: Make sure to update the `Exam` and `Question` classes to match the structure of the data in Firebase Firestore, and handle any additional fields or properties as needed.
