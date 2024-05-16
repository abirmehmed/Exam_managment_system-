To achieve the desired functionality of creating questions subcollections and retrieving/adding information, you can update the following methods in your `FirestoreManager` class:

1. **`createNewExam`** method:
   - Modify this method to create a subcollection for questions within each exam document.
   - Instead of directly saving questions to the exam document, create a subcollection named "questions" and add each question as a separate document within this subcollection.
   - Assign a unique document ID to each question document for easy retrieval and referencing.

2. **`updateExistingExam`** method:
   - Update this method to handle the changes in the question subcollection structure.
   - Retrieve the existing questions from the "questions" subcollection using the `retrieveExistingQuestions` method.
   - Save the updated questions to the "questions" subcollection, handling the addition, deletion, and modification of questions.

3. **`retrieveExistingQuestions`** method:
   - Modify this method to query the "questions" subcollection within the specified exam document.
   - Retrieve the question documents and extract the necessary information like text, type, options, and answer.
   - Set the document ID for each question using the retrieved document ID.

4. **`saveQuestionsToFirestore`** method:
   - Update this method to handle the changes in the question subcollection structure.
   - Instead of directly saving questions to the exam document, save each question as a separate document within the "questions" subcollection.
   - Use the `db.collection("exams").document(examId).collection("questions")` path to access the "questions" subcollection.

5. **`retrieveExamQuestions`** method:
   - Modify this method to query the "questions" subcollection within the specified exam document.
   - Retrieve the question documents and extract the necessary information like text, type, options, and answer.
   - Set the document ID for each question using the retrieved document ID.

6. **`deleteQuestionFromFirestore`** method:
   - Update this method to delete a specific question document from the "questions" subcollection within the specified exam document.
   - Use the `db.collection("exams").document(examId).collection("questions").document(questionDocumentId)` path to access and delete the question document.

By making these updates to the mentioned methods, you can effectively create questions subcollections within each exam document and manage the retrieval and addition of question information accordingly.
