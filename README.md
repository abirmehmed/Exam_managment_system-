After carefully reviewing the provided code, I can see that the issue with the `documentId` being `null` and the questions subfield not being generated in Firestore is likely due to the way the `questions` list is being passed to the `createNewExam` and `updateExistingExam` methods in the `ExamManager` class.

In the `ExamEditorActivity` class, when you create a new exam or update an existing exam, you're passing the `questionManager.getQuestions()` list to the `createNewExam` and `updateExistingExam` methods. However, this list may contain `Question` objects with `null` `documentId` values, which are considered deleted questions by the `saveQuestionsToFirestore` method.

Here's the relevant code from the `ExamEditorActivity` class:

```java
// Save the exam to Firestore
examManager.updateExistingExam(new ExamManager.OnExamUpdatedListener() {
    @Override
    public void onExamUpdated() {
        // Exam updated successfully
        Toast.makeText(ExamEditorActivity.this, "Exam saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onExamUpdateFailed(Exception e) {
        // Error updating exam
        Toast.makeText(ExamEditorActivity.this, "Failed to save exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
    }
});
```

And in the `onQuestionDeleted` method:

```java
public void onQuestionDeleted(Question question) {
    // Find the index of the question to be deleted
    int index = questionManager.getQuestions().indexOf(question);
    if (index != -1) {
        // Delete the question from the QuestionManager
        questionManager.deleteQuestion(index);
        // Notify the adapter about the data change
        questionAdapter.notifyItemRemoved(index);
        // Delete the question from Firestore
        FirestoreManager.getInstance().deleteQuestionFromFirestore(examManager.getExamId(), question.getDocumentId());
        // Update the exam in Firestore with the updated question list
        List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions);
    }
}
```

In the `onQuestionDeleted` method, when you delete a question, you're setting the `documentId` of the deleted `Question` object to `null`. However, this `Question` object with a `null` `documentId` is still present in the `questionManager.getQuestions()` list.

When you call `FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions)`, you're passing the `questionManager.getQuestions()` list, which may contain `Question` objects with `null` `documentId` values.

The `saveQuestionsToFirestore` method in the `FirestoreManager` class is designed to filter out `Question` objects with `null` `documentId` values, as they are considered deleted questions. This is done in the `filterOutDeletedQuestions` method:

```java
private List<Question> filterOutDeletedQuestions(List<Question> questions) {
    List<Question> updatedQuestions = new ArrayList<>();
    for (Question question : questions) {
        if (question.getDocumentId() != null) {
            updatedQuestions.add(question);
        }
    }
    return updatedQuestions;
}
```

As a result, when you call `updateExistingExam` with the `questionManager.getQuestions()` list, which may contain `Question` objects with `null` `documentId` values, the `saveQuestionsToFirestore` method filters out these questions, and they are not saved or updated in Firestore.

To resolve this issue, you should remove the `Question` objects with `null` `documentId` values from the `questionManager.getQuestions()` list before passing it to the `createNewExam` or `updateExistingExam` methods.

Here's an example of how you can modify the `onQuestionDeleted` method to remove the deleted `Question` object from the `questionManager.getQuestions()` list:

```java
public void onQuestionDeleted(Question question) {
    // Find the index of the question to be deleted
    int index = questionManager.getQuestions().indexOf(question);
    if (index != -1) {
        // Delete the question from the QuestionManager
        questionManager.deleteQuestion(index);
        // Notify the adapter about the data change
        questionAdapter.notifyItemRemoved(index);
        // Delete the question from Firestore
        FirestoreManager.getInstance().deleteQuestionFromFirestore(examManager.getExamId(), question.getDocumentId());
        // Remove the deleted question from the questionManager.getQuestions() list
        questionManager.getQuestions().remove(question);
        // Update the exam in Firestore with the updated question list
        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), questionManager.getQuestions());
    }
}
```

By removing the deleted `Question` object from the `questionManager.getQuestions()` list, you ensure that the list passed to the `updateExistingExam` method does not contain any `Question` objects with `null` `documentId` values.

Additionally, you should ensure that the `questions` list passed to the `createNewExam` method does not contain any `Question` objects with `null` `documentId` values.

After making these changes, the `saveQuestionsToFirestore` method should be able to correctly handle the creation and update of questions in Firestore, and the `documentId` field should be set correctly for new and existing questions.

Citations:
[1] https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/6629021/45e27353-64ad-4dc4-81a4-5ead2e2874a5/paste.txt
[2] https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/6629021/0cc0affd-38f1-464f-b08c-2a89829390ed/paste-2.txt
