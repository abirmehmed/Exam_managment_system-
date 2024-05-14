I apologize for the confusion earlier. Let's go through the issues you're facing step by step:

1. **Creating Exam Framework**:
   - In the `ExamManagementActivity`, you mentioned that clicking the "Create Exam Framework" button used to create an exam framework in Firestore, but it's not happening now.
   - To fix this, we need to update the `createAndLaunchExamEditor` method in the `ExamManagementActivity` to create a new exam document in Firestore before launching the `ExamEditorActivity`.

   ```java
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

               // Pass the exam data and examId to the ExamEditorActivity
               Intent intent = new Intent(ExamManagementActivity.this, ExamEditorActivity.class);
               intent.putExtra("examTitle", examTitle);
               intent.putExtra("examDate", examDate);
               intent.putExtra("examDuration", examDuration);
               intent.putExtra("examId", examId);
               startActivity(intent);
           })
           .addOnFailureListener(e -> {
               // Error creating exam document
               Toast.makeText(ExamManagementActivity.this, "Failed to create exam: " + e.getMessage(), Toast.LENGTH_SHORT).show();
           });
   }
   ```

   This method creates a new exam document in Firestore with the provided exam data and then launches the `ExamEditorActivity` with the exam data and the newly created `examId`.

2. **Adding Questions**:
   - In the `ExamEditorActivity`, you mentioned that clicking the "Add Question" button used to open the `QuestionEditorActivity`, but it's not happening now.
   - To fix this, you need to set up the click listener for the "Add Question" button and launch the `QuestionEditorActivity` when clicked.

   ```java
   // Initialize the Button for adding a question
   Button btnAddQuestion = findViewById(R.id.btn_add_question);
   btnAddQuestion.setOnClickListener(v -> {
       // Launch the QuestionEditorActivity
       Intent intent = new Intent(ExamEditorActivity.this, QuestionEditorActivity.class);
       startActivityForResult(intent, REQUEST_CODE_ADD_QUESTION);
   });
   ```

3. **Saving Questions**:
   - In the `ExamEditorActivity`, you mentioned that clicking the "Save" button in the `QuestionEditorActivity` doesn't save the question data to Firestore.
   - To fix this, you need to update the `onActivityResult` method in the `ExamEditorActivity` to handle the result from the `QuestionEditorActivity` and save the new question data to Firestore.

   ```java
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       if (requestCode == REQUEST_CODE_ADD_QUESTION && resultCode == RESULT_OK) {
           if (data != null) {
               String newQuestionJson = data.getStringExtra("newQuestion");
               if (newQuestionJson != null) {
                   Question newQuestion = GSON.fromJson(newQuestionJson, Question.class);

                   // Add the new question to the questions list
                   questions.add(newQuestion);

                   // Notify the adapter about the new question
                   if (questionAdapter != null) {
                       questionAdapter.notifyItemInserted(questions.size() - 1);
                   }

                   // Get the examId from the Intent extras
                   String examId = getIntent().getStringExtra("examId");

                   // Save the new question to Firestore
                   saveQuestionToFirestore(examId, newQuestion);
               }
           }
       }
   }

   private void saveQuestionToFirestore(String examId, Question question) {
       FirebaseFirestore db = FirebaseFirestore.getInstance();
       db.collection("exams")
           .document(examId)
           .collection("questions")
           .add(question)
           .addOnSuccessListener(documentReference -> {
               // Question document created successfully
           })
           .addOnFailureListener(e -> {
               // Error creating question document
               Toast.makeText(ExamEditorActivity.this, "Failed to save question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
           });
   }
   ```

   This code handles the result from the `QuestionEditorActivity`, adds the new question to the `questions` list, notifies the adapter about the new question, and then saves the new question to Firestore as a subcollection under the exam document.

4. **Saving Exam**:
   - In the `ExamEditorActivity`, you mentioned that clicking the "Save Exam" button doesn't save the exam data and questions to Firestore.
   - To fix this, you need to update the `onClick` listener for the "Save Exam" button to save the exam data and questions to Firestore.

   ```java
   // Set up the "Save Exam" button click listener
   Button btnSaveExam = findViewById(R.id.btn_save_exam);
   btnSaveExam.setOnClickListener(v -> {
       // Get the examId from the Intent extras
       String examId = getIntent().getStringExtra("examId");

       // Save the exam data and questions to Firestore
       saveExamToFirestore(examId, questions);
   });

   private void saveExamToFirestore(String examId, List<Question> questions) {
       FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                   Toast.makeText(ExamEditorActivity.this, "Failed to save question: " + e.getMessage(), Toast.LENGTH_SHORT).show();
               });
       }
   }
   ```

   This code gets the `examId` from the `Intent` extras and then saves all the questions in the `questions` list as subcollections under the exam document in Firestore.

By implementing these changes, you should be able to create exam frameworks, add questions, and save the exam data and questions to Firestore. If you're still facing issues, please provide more details or error messages, and I'll do my best to assist you further.
