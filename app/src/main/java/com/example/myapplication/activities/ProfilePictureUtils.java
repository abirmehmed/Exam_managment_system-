package com.example.myapplication.activities;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ProfilePictureUtils {
    private static final String PROFILE_PICTURES_DIRECTORY = "profile_pictures";

    public static void uploadProfilePicture(Context context, Uri imageUri) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // Handle the case when the user is not authenticated
            return;
        }

        String currentUserUid = currentUser.getUid();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profilePicturesRef = storageReference.child(PROFILE_PICTURES_DIRECTORY);

        profilePicturesRef.getMetadata().addOnSuccessListener(metadata -> {
            // The "profile_pictures" directory exists
            uploadImageToProfilePicturesDirectory(context, imageUri, profilePicturesRef, currentUserUid);
        }).addOnFailureListener(exception -> {
            // The "profile_pictures" directory does not exist
            createProfilePicturesDirectory(profilePicturesRef, context, imageUri, currentUserUid);
        });
    }

    private static void createProfilePicturesDirectory(StorageReference profilePicturesRef, Context context, Uri imageUri, String currentUserUid) {
        byte[] emptyBytes = new byte[0];
        UploadTask uploadTask = profilePicturesRef.putBytes(emptyBytes);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // The "profile_pictures" directory was created successfully
            uploadImageToProfilePicturesDirectory(context, imageUri, profilePicturesRef, currentUserUid);
        }).addOnFailureListener(exception -> {
            // Failed to create the "profile_pictures" directory
            Log.e("ProfilePictureUtils", "Failed to create profile pictures directory", exception);
            Toast.makeText(context, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
        });
    }

    private static void uploadImageToProfilePicturesDirectory(Context context, Uri imageUri, StorageReference profilePicturesRef, String currentUserUid) {
        StorageReference imageRef = profilePicturesRef.child(currentUserUid + ".jpg");

        UploadTask uploadTask = imageRef.putFile(imageUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Image uploaded successfully
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                storeProfilePictureUrl(context, downloadUrl);
            }).addOnFailureListener(exception -> {
                Log.e("ProfilePictureUtils", "Failed to get download URL", exception);
                Toast.makeText(context, "Failed to get download URL", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(exception -> {
            // Handle upload failure
            Log.e("ProfilePictureUtils", "Image upload failed", exception);
            Toast.makeText(context, "Failed to upload profile picture", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Track upload progress
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d("ProfilePictureUtils", "Upload progress: " + progress + "%");
        });
    }

    private static void storeProfilePictureUrl(Context context, String downloadUrl) {
        // Store the download URL in your database
        // For example, if you're using Firebase Firestore:
        FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .update("profilePictureUrl", downloadUrl)
                .addOnSuccessListener(aVoid -> {
                    // Profile picture URL stored successfully
                    Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Handle failure
                    Toast.makeText(context, "Failed to update profile picture", Toast.LENGTH_SHORT).show();
                });
    }
}
