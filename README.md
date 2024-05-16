Yes, you can add the functionality to upload a profile picture from the gallery or camera in your Android app. Here's how you can implement it:

1. **Add Permissions**
   You need to request permissions from the user to access the camera and external storage (for accessing the gallery). Add the following permissions to your `AndroidManifest.xml` file:

   ```xml
   <uses-permission android:name="android.permission.CAMERA" />
   <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
   ```

2. **Create a Method to Open the Image Picker**
   Create a method that opens the image picker and allows the user to select an image from the gallery or take a new photo using the camera. You can use the `Intent.ACTION_PICK` and `Intent.ACTION_IMAGE_CAPTURE` intents for this purpose.

   ```java
   private static final int REQUEST_IMAGE_PICK = 1;
   private static final int REQUEST_IMAGE_CAPTURE = 2;

   private void openImagePicker() {
       AlertDialog.Builder builder = new AlertDialog.Builder(this);
       builder.setTitle("Select Image Source");
       builder.setItems(new CharSequence[]{"Gallery", "Camera"}, (dialog, which) -> {
           switch (which) {
               case 0:
                   openGallery();
                   break;
               case 1:
                   openCamera();
                   break;
           }
       });
       builder.create().show();
   }

   private void openGallery() {
       Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
       startActivityForResult(intent, REQUEST_IMAGE_PICK);
   }

   private void openCamera() {
       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
       if (intent.resolveActivity(getPackageManager()) != null) {
           File photoFile = createImageFile();
           if (photoFile != null) {
               Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
               intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
               startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
           }
       }
   }
   ```

3. **Handle the Result**
   Override the `onActivityResult` method to handle the result from the image picker or camera. You can then upload the selected or captured image to your desired location (e.g., Firebase Storage).

   ```java
   @Override
   protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
       super.onActivityResult(requestCode, resultCode, data);
       if (resultCode == RESULT_OK) {
           if (requestCode == REQUEST_IMAGE_PICK) {
               Uri imageUri = data.getData();
               // Upload the selected image to your desired location
               uploadImageToFirebaseStorage(imageUri);
           } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
               File photoFile = createImageFile();
               if (photoFile != null) {
                   Uri photoURI = FileProvider.getUriForFile(this, "com.example.myapplication.fileprovider", photoFile);
                   // Upload the captured image to your desired location
                   uploadImageToFirebaseStorage(photoURI);
               }
           }
       }
   }
   ```

4. **Upload the Image to Firebase Storage**
   Create a method to upload the selected or captured image to Firebase Storage. You can use the Firebase Storage SDK for this purpose.

   ```java
   private void uploadImageToFirebaseStorage(Uri imageUri) {
       StorageReference storageRef = FirebaseStorage.getInstance().getReference();
       StorageReference imageRef = storageRef.child("profile_pictures/" + System.currentTimeMillis() + ".jpg");

       UploadTask uploadTask = imageRef.putFile(imageUri);
       uploadTask.addOnSuccessListener(taskSnapshot -> {
           // Image uploaded successfully
           // You can retrieve the download URL and store it in your database
           imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
               String downloadUrl = uri.toString();
               // Store the download URL in your database
               storeProfilePictureUrl(downloadUrl);
           });
       }).addOnFailureListener(exception -> {
           // Handle upload failure
           Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show();
       });
   }
   ```

5. **Store the Profile Picture URL**
   Create a method to store the download URL of the uploaded image in your database (e.g., Firebase Firestore or Realtime Database).

   ```java
   private void storeProfilePictureUrl(String downloadUrl) {
       // Store the download URL in your database
       // For example, if you're using Firebase Firestore:
       FirebaseFirestore.getInstance().collection("users")
               .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
               .update("profilePictureUrl", downloadUrl)
               .addOnSuccessListener(aVoid -> {
                   // Profile picture URL stored successfully
                   Toast.makeText(this, "Profile picture updated", Toast.LENGTH_SHORT).show();
               })
               .addOnFailureListener(e -> {
                   // Handle failure
                   Toast.makeText(this, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
               });
   }
   ```

6. **Set Up the Click Listener**
   Set up a click listener for the profile picture button (`iv_profile_picture`) in your layout to open the image picker when clicked.

   ```java
   ImageView profilePictureButton = findViewById(R.id.iv_profile_picture);
   profilePictureButton.setOnClickListener(v -> openImagePicker());
   ```

By following these steps, you can add the functionality to upload a profile picture from the gallery or camera in your Android app. Make sure to handle the necessary permissions and replace the placeholders (e.g., `"com.example.myapplication.fileprovider"`) with your actual package name and file provider authority.
