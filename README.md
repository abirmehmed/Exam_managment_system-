Based on the data you provided from the Firebase database, it seems that the issue is with the way the questions are being retrieved and displayed on the front-end.

In the `FirestoreManager` class, the `retrieveExamQuestions` method is responsible for retrieving the questions from Firestore and creating `Question` objects. Here's the code for that method:

```java
public Task<List<Question>> retrieveExamQuestions(String examId) {
    return db.collection("exams")
            .document(examId)
            .collection("questions")
            .get()
            .continueWith(task -> {
                List<Question> questions = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    String text = document.getString("text");
                    QuestionType type = QuestionType.valueOf(document.getString("type"));
                    List<String> options = (List<String>) document.get("options");
                    String answer = document.getString("answer");
                    Question question = new Question(text, type, options, answer);
                    questions.add(question);
                }
                return questions;
            });
}
```

The issue might be occurring due to the way the questions are being stored in the Firestore database. Based on the data you provided, it seems that the questions are stored as separate documents within the "questions" collection, and each document contains the question data.

To fix this issue, you need to modify the `retrieveExamQuestions` method to handle the case where multiple questions are stored as separate documents. Here's how you can update the method:

```java
public Task<List<Question>> retrieveExamQuestions(String examId) {
    return db.collection("exams")
            .document(examId)
            .collection("questions")
            .get()
            .continueWith(task -> {
                List<Question> questions = new ArrayList<>();
                for (DocumentSnapshot document : task.getResult().getDocuments()) {
                    Map<String, Object> questionData = document.getData();
                    String text = (String) questionData.get("text");
                    QuestionType type = QuestionType.valueOf((String) questionData.get("type"));
                    List<String> options = (List<String>) questionData.get("options");
                    String answer = (String) questionData.get("answer");
                    Question question = new Question(text, type, options, answer);
                    questions.add(question);
                }
                return questions;
            });
}
```

In this updated code, we're using `document.getData()` to retrieve the question data as a `Map<String, Object>`. Then, we're accessing the individual fields of the question data using the appropriate keys (`"text"`, `"type"`, `"options"`, and `"answer"`).

By making this change, the `retrieveExamQuestions` method should be able to handle the case where multiple questions are stored as separate documents in the "questions" collection, and it should retrieve all the questions correctly.

After updating the `retrieveExamQuestions` method, the questions should be displayed correctly on the front-end without any duplicates.
