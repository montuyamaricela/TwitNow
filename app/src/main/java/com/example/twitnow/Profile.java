package com.example.twitnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.twitnow.adapter.PostRecyclerAdapter;
import com.example.twitnow.model.PostModel;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Query;

public class Profile extends AppCompatActivity {

    ImageView profile, backBtn, userProfile;
    TextView userFullname, userUsername;
    FirebaseAuth auth;
    FirebaseUser user;
    UserModel userModel;
    PostRecyclerAdapter adapter;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        backBtn = findViewById(R.id.back_btn);
        profile = findViewById(R.id.profile);
        userProfile = findViewById(R.id.userProfile);
        userFullname = findViewById(R.id.userfullname);
        userUsername = findViewById(R.id.user_Username);
        recyclerView = findViewById(R.id.post_recyclerView);


        if (user == null){
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
            finish();
        } else {
            fetchUserDetails();
        }

        backBtn.setOnClickListener(v-> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        });

        setUpRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_profile), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void setUpRecyclerView() {
        Query query = FirebaseUtil.allPostCollectionReference().whereEqualTo("userID", FirebaseUtil.currentUserId())
                .orderBy("postedOn", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        adapter = new PostRecyclerAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);

            }
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
                        Glide.with(getApplicationContext()).load(userModel.getImage()).into(userProfile);
                    }
                    userFullname.setText(userModel.getFullName());
                    userUsername.setText(userModel.getUsername());

                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}