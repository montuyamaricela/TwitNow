package com.example.twitnow;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitnow.adapter.ChatRecyclerAdapter;
import com.example.twitnow.adapter.SearchUserRecyclerAdapter;
import com.example.twitnow.model.ChatMessageModel;
import com.example.twitnow.model.ChatRoomModel;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.AndroidUtil;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

import java.util.Arrays;
import java.util.Objects;

public class ChatActivity extends AppCompatActivity {


    UserModel otherUser;
    String chatRoomID;
    ChatRoomModel chatRoomModel;

    EditText messageInput;
    ImageButton sendMessage;
    ImageView backBtn, profile;
    TextView otherUsername;
    RecyclerView recyclerView;
    ChatRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());

        chatRoomID = FirebaseUtil.getChatRoomId(Objects.requireNonNull(FirebaseUtil.currentUserId()), otherUser.getUserID());


        messageInput = findViewById(R.id.chat_message_input);
        sendMessage = findViewById(R.id.message_send_btn);
        backBtn = findViewById(R.id.back_btn);
        profile = findViewById(R.id.profile);
        otherUsername = findViewById(R.id.other_username);
        recyclerView = findViewById(R.id.chat_recyclerView);

        // Handle back button click
        backBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
//            startActivity(intent);
//            finish();
            onBackPressed();
        });

        otherUsername.setText(otherUser.getFullName());
        if (otherUser.getImage() != null){
            AndroidUtil.setProfilePic(this, otherUser.getImage(), profile);
        }
        sendMessage.setOnClickListener(v-> {
            String message = messageInput.getText().toString().trim();
            if (message.isEmpty()){
                return;
            }
            sendMessageToUser(message);
        });

        getOrCreateChatRoomModel();
        setupChatRecyclerView();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    void setupChatRecyclerView(){
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomID).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        adapter = new ChatRecyclerAdapter(options, getApplicationContext());

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(-10);
            }
        });

    }

    void getOrCreateChatRoomModel(){
        FirebaseUtil.getChatRoomReference(chatRoomID).get().addOnCompleteListener(task-> {
            if (task.isSuccessful()){
                chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                if (chatRoomModel == null){
                    chatRoomModel = new ChatRoomModel(
                            chatRoomID,
                            Arrays.asList(FirebaseUtil.currentUserId(), otherUser.getUserID()),
                            Timestamp.now(),
                            ""
                    );
                    FirebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);
                }
            }
        });
    }

    void sendMessageToUser(String message){

        chatRoomModel.setLastMessageTimeStamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        chatRoomModel.setLastMessage(message);
        FirebaseUtil.getChatRoomReference(chatRoomID).set(chatRoomModel);



        ChatMessageModel chatMessageModel = new ChatMessageModel(message, FirebaseUtil.currentUserId(), Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatRoomID).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){
                    messageInput.setText("");
                }
            }
        });
    }
}