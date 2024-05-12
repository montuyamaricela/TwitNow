package com.example.twitnow;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

public class EditProfile extends AppCompatActivity {
    StorageReference storageReference;
    Uri image;
    FirebaseAuth auth;
    ImageView logout, profile, userProfile;
    FirebaseUser user;
    EditText userEmail, userFullName, usernameInput;
    Button updateUser;
    UserModel userModel;
    ProgressBar progressBar;

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK){
                image = result.getData().getData();
                Glide.with(getApplicationContext()).load(image).into(userProfile);
            } else {
                Toast.makeText(EditProfile.this, "Please select an image", Toast.LENGTH_SHORT).show();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logout);
        profile = findViewById(R.id.profile);
        userProfile = findViewById(R.id.userProfile);
        userEmail = findViewById(R.id.email);
        userFullName = findViewById(R.id.name);
        usernameInput = findViewById(R.id.username);
        updateUser = findViewById(R.id.updateUserDetails);
        progressBar = findViewById(R.id.progressBar);
        storageReference = FirebaseStorage.getInstance().getReference();

        user = auth.getCurrentUser();

        // if the user is not logged in, open login activity
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {
            fetchUserDetails();
            userEmail.setText(user.getEmail());
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


        // upload image
        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                activityResultLauncher.launch(intent);
            }
        });


        updateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserDetails();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.editProfile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                    Toast.makeText(EditProfile.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            userModel = new UserModel(fullName, username, imageUrl != null ? imageUrl.toString() : null, user.getEmail(), FirebaseUtil.currentUserId()); // Provide imageUrl as the third argument
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(task -> {
            progressBar.setVisibility(View.GONE);
            updateUser.setVisibility(View.VISIBLE);
            if (task.isSuccessful()) {
                // Direct to profile page
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(EditProfile.this, "Failed to update user details: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    void fetchUserDetails(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                progressBar.setVisibility(View.GONE);
                updateUser.setVisibility(View.VISIBLE);
                if (task.isSuccessful()){
                    userModel = task.getResult().toObject(UserModel.class);
                    if (userModel != null){
                        usernameInput.setText("@"+userModel.getUsername());
                        userFullName.setText(userModel.getFullName());

                        // Load image into userProfile ImageView
                        if (userModel.getImage() != null && !userModel.getImage().isEmpty()) {
                            Glide.with(getApplicationContext()).load(userModel.getImage()).into(userProfile);

                            // appbar profile here
                            Glide.with(getApplicationContext()).load(userModel.getImage()).into(profile);
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
            }
        });
    }

}