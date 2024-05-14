Based on the information provided, it seems that the "Invalid Exam ID" toast is being displayed when the `examId` is null or empty when launching the `ExamEditorActivity`. This could happen if the `examId` is not being passed correctly from the `ExamManagementActivity`.

To troubleshoot this issue, you can follow these steps:

1. **Add Logging in `ExamManagementActivity`**:
   In the `ExamManagementActivity`, add logging statements to print the value of the `examId` before launching the `ExamEditorActivity`. This will help you verify if the `examId` is being retrieved correctly.

   For example, in the `onExamClick` method:

   ```java
   @Override
   public void onExamClick(Exam exam) {
       // Log the examId value
       Log.d("ExamManagementActivity", "examId: " + exam.getExamId());

       // Launch the ExamEditorActivity and pass the exam data
       Intent intent = new Intent(this, ExamEditorActivity.class);
       intent.putExtra("examTitle", exam.getTitle());
       intent.putExtra("examDate", exam.getDate());
       intent.putExtra("examDuration", exam.getDuration());
       intent.putExtra("examId", exam.getExamId());
       startActivity(intent);
   }
   ```

2. **Check Firestore Data**:
   Open the Firestore console and verify that the `examId` field is correctly stored in the exam documents. If the `examId` field is missing or incorrect, it could be the reason why the "Invalid Exam ID" toast is being displayed.

3. **Verify `Exam` Class**:
   Ensure that the `Exam` class has a proper `getExamId()` method that returns the correct `examId` value. If the `examId` field is not being set correctly when creating or retrieving the `Exam` objects, the `getExamId()` method will return null or an incorrect value.

4. **Check `ExamEditorActivity` Code**:
   In the `ExamEditorActivity`, double-check the code where you retrieve the `examId` from the `Intent` extras. Make sure that you're handling the case where the `examId` is null or empty correctly.

   ```java
   String examId = getIntent().getStringExtra("examId");
   if (examId != null && !examId.isEmpty()) {
       // Proceed with retrieving and displaying the exam data
   } else {
       // Handle the case where examId is null or empty
       Toast.makeText(ExamEditorActivity.this, "Invalid exam ID", Toast.LENGTH_SHORT).show();
   }
   ```

By following these steps, you should be able to identify the root cause of the "Invalid Exam ID" issue and resolve it accordingly. If you're still facing issues after trying these steps, please provide any additional logs or information that might help in further troubleshooting.

Citations:
[1] https://stackoverflow.com/questions/61028079/firebase-cli-always-shows-error-invalid-project-id
[2] https://stackoverflow.com/questions/52354743/cannot-resolve-firebasefirestore-android
[3] https://stackoverflow.com/questions/67845455/could-not-reach-cloud-firestore-backend-connection-failed-1-times
[4] https://firebase.google.com/docs/firestore/query-data/queries
[5] https://github.com/firebase/flutterfire/issues/1979
