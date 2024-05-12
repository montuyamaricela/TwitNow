package com.example.twitnow;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.bumptech.glide.Glide;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.FirebaseUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseUser user;
    UserModel userModel;
    ImageView profile, search;

    BottomNavigationView bottomNavigationView;

    ChatFragment chatFragment;
    ProfileFragment profileFragment;
    homeFragment homeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        search = findViewById(R.id.search);
        profile = findViewById(R.id.profile);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        chatFragment = new ChatFragment();
        profileFragment = new ProfileFragment();
        homeFragment = new homeFragment();

        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.menu_chats) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, chatFragment).commit();
            } else if (menuItem.getItemId() == R.id.menu_profile) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, profileFragment).commit();
            } else if (menuItem.getItemId() == R.id.menu_home) {
                getSupportFragmentManager().beginTransaction().replace(R.id.main_frame_layout, homeFragment).commit();
            }
            return true; // Return true for other menu items
        });

        bottomNavigationView.setSelectedItemId(R.id.menu_home);

        // if the user is not logged in, open login activity
        if (user == null){
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {
            fetchUserDetails();
        }

        search.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
            finish();
        });

        profile.setOnClickListener(v-> {
            goToEditProfile();
        });

        getFCMToken();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0); // Remove bottom padding
            return insets;
        });

    }

    void fetchUserDetails(){
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    // Load image into userProfile ImageView
                    if (userModel.getImage() != null && !userModel.getImage().isEmpty()) {
                        Glide.with(getApplicationContext()).load(userModel.getImage()).into(profile);
                    }
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.menu_profile);
                }
            }
        });
    }

    public void goToEditProfile(){
        Intent intent = new Intent(getApplicationContext(), Profile.class);
        startActivity(intent);
        finish();
    }

    void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String token = task.getResult();
                Log.i("My Token here-> ", token);
//                FirebaseUtil.currentUserDetails().update("fcmToken",token);

            }
        });
    }

}