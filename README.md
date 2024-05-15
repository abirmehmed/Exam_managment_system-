The error you're encountering, `java.lang.NullPointerException: Provided document path must not be null`, is occurring in the `deleteQuestionFromFirestore` method of your `FirestoreManager` class.

Here's the relevant code from your `FirestoreManager` class:

```java
public void deleteQuestionFromFirestore(String examId, String questionDocumentId) {
    db.collection("exams")
            .document(examId)
            .collection("questions")
            .document(questionDocumentId)
            .delete();
}
```

The error is likely caused by the `questionDocumentId` being `null` when you're trying to delete a question from Firestore. The `document` method in Firestore expects a non-null document ID, and passing a `null` value results in this `NullPointerException`.

To resolve this issue, you need to ensure that the `questionDocumentId` is not `null` before calling the `delete` method. You can add a null check in the `deleteQuestionFromFirestore` method:

```java
public void deleteQuestionFromFirestore(String examId, String questionDocumentId) {
    if (questionDocumentId != null) {
        db.collection("exams")
                .document(examId)
                .collection("questions")
                .document(questionDocumentId)
                .delete();
    }
}
```

By adding this null check, you'll prevent the `NullPointerException` from occurring when the `questionDocumentId` is `null`.

As for the issue with the `documentId` being `null` and questions being deleted when saving the exam, it's possible that the `saveQuestionsToFirestore` method is not working as expected. Double-check the implementation of this method and ensure that the `documentId` field is being set correctly for both new and existing questions.

Additionally, make sure that you're correctly handling the deletion of questions in the `saveQuestionsToFirestore` method. If you're not removing the deleted questions from the `newQuestions` and `existingQuestions` lists before saving, it could lead to unexpected behavior.

Here's an example of how you could handle the deletion of questions in the `saveQuestionsToFirestore` method:

```java
private Task<Void> saveQuestionsToFirestore(String examId, List<Question> newQuestions, List<Question> existingQuestions) {
    return db.runTransaction(transaction -> {
        WriteBatch batch = db.batch();
        Set<String> allQuestionIds = new HashSet<>();

        // Remove deleted questions from the lists
        List<Question> updatedNewQuestions = new ArrayList<>(newQuestions);
        List<Question> updatedExistingQuestions = new ArrayList<>(existingQuestions);
        for (Question question : newQuestions) {
            if (question.getDocumentId() == null) {
                updatedNewQuestions.remove(question);
            }
        }
        for (Question question : existingQuestions) {
            if (question.getDocumentId() == null) {
                updatedExistingQuestions.remove(question);
            }
        }

        // Add existing question IDs to the set
        for (Question existingQuestion : updatedExistingQuestions) {
            allQuestionIds.add(existingQuestion.getDocumentId());
        }

        // Delete removed questions from Firestore
        for (Question removedQuestion : updatedExistingQuestions) {
            batch.delete(db.collection("exams").document(examId).collection("questions").document(removedQuestion.getDocumentId()));
        }

        // Save new questions and update existing questions
        for (Question question : updatedNewQuestions) {
            DocumentReference questionRef;
            if (allQuestionIds.contains(question.getDocumentId())) {
                // Update existing question
                questionRef = db.collection("exams").document(examId).collection("questions").document(question.getDocumentId());
            } else {
                // Save new question
                questionRef = db.collection("exams").document(examId).collection("questions").document();
                batch.set(questionRef, question.toMap());
                question.setDocumentId(questionRef.getId());
                allQuestionIds.add(questionRef.getId());
            }
        }

        // Update existing questions
        for (Question existingQuestion : updatedExistingQuestions) {
            if (!allQuestionIds.contains(existingQuestion.getDocumentId())) {
                DocumentReference questionRef = db.collection("exams").document(examId).collection("questions").document(existingQuestion.getDocumentId());
                batch.set(questionRef, existingQuestion.toMap());
            }
        }

        // Commit the batch and return the resulting Task<Void>
        return batch.commit();
    }).continueWithTask(task -> {
        if (task.isSuccessful()) {
            return task.getResult();
        } else {
            throw task.getException();
        }
    });
}
```

In this updated implementation, we're creating new lists `updatedNewQuestions` and `updatedExistingQuestions` by copying the original `newQuestions` and `existingQuestions` lists. We then remove any questions with a `null` `documentId` from these lists, as these are the questions that should be considered deleted.

The rest of the method remains the same, but it operates on the `updatedNewQuestions` and `updatedExistingQuestions` lists instead of the original lists.

By implementing these changes, you should be able to resolve the `NullPointerException` when deleting questions and ensure that the `documentId` field is set correctly for both new and existing questions.
