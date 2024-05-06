package com.example.twitnow;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


// Import statements

public class ProfileFragment extends Fragment {

    StorageReference storageReference;
    Uri image;
    FirebaseAuth auth;
    ImageView userProfile;
    FirebaseUser user;
    EditText userEmail, userFullName, usernameInput;
    Button updateUser;
    UserModel userModel;
    ProgressBar progressBar;
    TextView logout;

//    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
//        if (result.getResultCode() == Activity.RESULT_OK) {
//            Intent data = result.getData();
//            if (data != null) {
//                Uri imageData = data.getData();
//                if (imageData != null && userProfile != null && getContext() != null) {
//                    Glide.with(requireContext()).load(imageData).into(userProfile);
//                } else {
//                    Toast.makeText(requireContext(), "Image data or ImageView is null", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Toast.makeText(requireContext(), "Intent data is null", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
//        }
//    });

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == Activity.RESULT_OK){
                image = result.getData().getData();
                Glide.with(requireContext().getApplicationContext()).load(image).into(userProfile);
            } else {
                Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        auth = FirebaseAuth.getInstance();
        userProfile = view.findViewById(R.id.userProfile);
        userEmail = view.findViewById(R.id.email);
        userFullName = view.findViewById(R.id.name);
        usernameInput = view.findViewById(R.id.username);
        updateUser = view.findViewById(R.id.updateUserDetails);
        progressBar = view.findViewById(R.id.progressBar);
        logout = view.findViewById(R.id.logout);
        storageReference = FirebaseStorage.getInstance().getReference();

        user = auth.getCurrentUser();

        // if the user is not logged in, open login activity
        if (user == null) {
            Intent intent = new Intent(requireContext(), login.class);
            startActivity(intent);
        } else {
            fetchUserDetails();
            userEmail.setText(user.getEmail());
        }


        updateUser.setOnClickListener(v -> setUserDetails());
        logout.setOnClickListener(v-> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(requireContext().getApplicationContext(), login.class);
            startActivity(intent);
        });

        userProfile.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            activityResultLauncher.launch(intent);
        });
        return view;
    }


    void setUserDetails() {
        progressBar.setVisibility(View.VISIBLE);
        updateUser.setVisibility(View.GONE);
        String username = usernameInput.getText().toString();
        String fullName = userFullName.getText().toString();
        if (username.isEmpty() || username.length() < 3) {
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }

        if (fullName.isEmpty() || fullName.length() < 2) {
            userFullName.setError("Full name length should be at least 2 chars");
            return;
        }

        if (image == null) {
            // If no image is selected, proceed with only user details update
            updateUserDetails(null, username, fullName);
        } else {
            // If image is selected, upload the image to Firebase Storage
            uploadImage(username, fullName);
        }
    }

    void uploadImage(final String username, final String fullName) {
        // Get reference to the image location in Firebase Storage
        StorageReference imageRef = storageReference.child("profileImages/" + user.getUid());

        // Upload the image to Firebase Storage
        imageRef.putFile(image)
                .addOnSuccessListener(taskSnapshot -> {
                    // Image uploaded successfully, get the download URL
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Update user details with image URL
                        updateUserDetails(uri, username, fullName);
                    });
                })
                .addOnFailureListener(e -> {
                    // Image upload failed
                    progressBar.setVisibility(View.GONE);
                    updateUser.setVisibility(View.VISIBLE);
                    Toast.makeText(requireContext(), "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    void updateUserDetails(Uri imageUrl, String username, String fullName) {
        if (userModel != null) {
            userModel.setUsername(username);
            userModel.setFullName(fullName);
            if (imageUrl != null) {
                userModel.setImage(imageUrl.toString()); // Change to setImage()
            }
        } else {
            userModel = new UserModel(fullName, username, imageUrl != null ? imageUrl.toString() : null); // Provide imageUrl as the third argument
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            updateUser.setVisibility(View.VISIBLE);
            if (task.isSuccessful()) {
                // Direct to profile page
                Intent intent = new Intent(requireContext(), MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(requireContext(), "Failed to update user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    void fetchUserDetails(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            updateUser.setVisibility(View.VISIBLE);
            if (task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null){
                    usernameInput.setText(userModel.getUsername());
                    userFullName.setText(userModel.getFullName());

                    // Load image into userProfile ImageView
                    if (userModel.getImage() != null && !userModel.getImage().isEmpty()) {
                        Glide.with(requireContext().getApplicationContext()).load(userModel.getImage()).into(userProfile);
                    }

                    // Check if the username already exists
                    if (userModel.getUsername() != null && !userModel.getUsername().isEmpty()) {
                        // If username exists, set EditText as non-editable
                        usernameInput.setEnabled(false);
                        usernameInput.setFocusable(false);
                        usernameInput.setFocusableInTouchMode(false);
                    }
                }
            }
        });
    }
}
