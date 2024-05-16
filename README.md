Sure, here's a step-by-step algorithmic approach to implement the code for creating the "profile_pictures" directory if it doesn't exist, and then uploading the image to that directory:

1. **In the `TeacherHomepageActivity` class**
   - Add the following method to create the "profile_pictures" directory if it doesn't exist:

     ```java
     private void createProfilePicturesDirectory(StorageReference profilePicturesRef) {
         byte[] emptyBytes = new byte[0];
         UploadTask uploadTask = profilePicturesRef.putBytes(emptyBytes);
         uploadTask.addOnSuccessListener(taskSnapshot -> {
             // The "profile_pictures" directory was created successfully
             Log.d("TeacherHomepageActivity", "Profile pictures directory created");
         }).addOnFailureListener(exception -> {
             // Failed to create the "profile_pictures" directory
             Log.e("TeacherHomepageActivity", "Failed to create profile pictures directory", exception);
         });
     }
     ```

2. **In the `TeacherHomepageActivity` class**
   - Modify the `uploadImageToFirebaseStorage` method as follows:

     ```java
     private void uploadImageToFirebaseStorage(Uri imageUri) {
         FirebaseStorage.getInstance().setMaxUploadRetryTimeMillis(2000);

         StorageReference storageRef = FirebaseStorage.getInstance().getReference();
         StorageReference profilePicturesRef = storageRef.child("profile_pictures");

         profilePicturesRef.getMetadata().addOnSuccessListener(metadata -> {
             // The "profile_pictures" directory exists
             uploadImageToProfilePicturesDirectory(imageUri, profilePicturesRef);
         }).addOnFailureListener(exception -> {
             // The "profile_pictures" directory does not exist
             createProfilePicturesDirectory(profilePicturesRef);
             uploadImageToProfilePicturesDirectory(imageUri, profilePicturesRef);
         });
     }
     ```

3. **In the `TeacherHomepageActivity` class**
   - Add the following method to upload the image to the "profile_pictures" directory:

     ```java
     private void uploadImageToProfilePicturesDirectory(Uri imageUri, StorageReference profilePicturesRef) {
         String currentUserUid = currentUser.getUid();
         Log.d("TeacherHomepageActivity", "Current user UID: " + currentUserUid);

         String storagePath = "profile_pictures/" + currentUserUid + ".jpg";
         Log.d("TeacherHomepageActivity", "Storage path: " + storagePath);

         StorageReference imageRef = profilePicturesRef.child(currentUserUid + ".jpg");

         UploadTask uploadTask = imageRef.putFile(imageUri);
         // ... (the rest of your upload code)
     }
     ```

4. **In the `TeacherHomepageActivity` class**
   - Call the `uploadImageToFirebaseStorage` method when you want to upload the image (e.g., in the `onActivityResult` method or when the user clicks a button).

By following this algorithmic approach, you'll be able to create the "profile_pictures" directory if it doesn't exist, and then upload the image to that directory. The code is organized into separate methods for better readability and maintainability.

Make sure to import the necessary classes at the top of your `TeacherHomepageActivity` class:

```java
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
```

If you encounter any issues or have further questions, feel free to ask.
