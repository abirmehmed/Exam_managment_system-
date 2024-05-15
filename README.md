Sure, let's break it down into a step-by-step algorithmic approach. Here's what you need to do:

**Step 1: Update the `onActivityResult` method in the `ExamEditorActivity` class**

After updating the exam in Firestore with the new or updated question, refresh the `RecyclerView` by calling `questionRecyclerView.setAdapter(questionAdapter)` or `questionAdapter.notifyDataSetChanged()`.

```java
@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_ADD_QUESTION && resultCode == RESULT_OK) {
        // ... (existing code for adding a new question)

        // Update the exam in Firestore with the new question
        List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions);

        // Refresh the RecyclerView
        questionRecyclerView.setAdapter(questionAdapter);
        // OR
        // questionAdapter.notifyDataSetChanged();
    } else if (requestCode == REQUEST_CODE_EDIT_QUESTION && resultCode == RESULT_OK) {
        // ... (existing code for editing a question)

        // Update the exam in Firestore with the updated question
        List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions);

        // Refresh the RecyclerView
        questionRecyclerView.setAdapter(questionAdapter);
        // OR
        // questionAdapter.notifyDataSetChanged();
    }
}
```

**Step 2: Update the `onQuestionDelete` method in the `ExamEditorActivity` class**

After deleting the question from Firestore, update the exam in Firestore with the updated question list, and then refresh the `RecyclerView`.

```java
public void onQuestionDelete(Question question) {
    // Find the index of the question to be deleted
    int index = questionManager.getQuestions().indexOf(question);

    if (index != -1) {
        // Delete the question from the QuestionManager
        questionManager.deleteQuestion(index);

        // Notify the adapter about the data change
        questionAdapter.notifyItemRemoved(index);

        // Delete the question from Firestore
        FirestoreManager.getInstance().deleteQuestionFromFirestore(examManager.getExamId(), question);

        // Update the exam in Firestore with the updated question list
        List<Question> updatedQuestions = new ArrayList<>(questionManager.getQuestions());
        FirestoreManager.getInstance().updateExistingExam(examManager.getExamId(), examManager.getExamTitle(), examManager.getExamDate(), examManager.getExamDuration(), updatedQuestions);

        // Refresh the RecyclerView
        questionRecyclerView.setAdapter(questionAdapter);
        // OR
        // questionAdapter.notifyDataSetChanged();
    }
}
```

By following these steps, you'll ensure that the changes made to the questions are reflected immediately on the front-end after updating the data in Firestore.
