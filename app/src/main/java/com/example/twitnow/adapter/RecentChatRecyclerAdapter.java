package com.example.twitnow.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitnow.ChatActivity;
import com.example.twitnow.R;
import com.example.twitnow.model.ChatRoomModel;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.AndroidUtil;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {

    Context context;
    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int i, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatroom(model.getUserIds()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                boolean lastMessageSentByMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                if (otherUserModel != null && otherUserModel.getImage() != null) {
                    AndroidUtil.setProfilePic(context, otherUserModel.getImage(), holder.userProfile);
                }
                holder.fullname.setText(otherUserModel != null ? otherUserModel.getFullName() : null);
                if (lastMessageSentByMe){
                    holder.lastMessage.setText("You: "+ model.getLastMessage());
                } else holder.lastMessage.setText(model.getLastMessage());
                holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimeStamp()));

                holder.itemView.setOnClickListener(v-> {
                    //navigate to chat activity
                    if (otherUserModel != null){
                        Intent intent = new Intent(context, ChatActivity.class);
                        AndroidUtil.passUserModelAsIntent(intent, otherUserModel);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                });
            }
        });
    }

    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    static class ChatRoomModelViewHolder extends RecyclerView.ViewHolder{

        TextView fullname, lastMessage, lastMessageTime;
        ImageView userProfile;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.user_fullname);
            lastMessage = itemView.findViewById(R.id.last_message_text);
            lastMessageTime = itemView.findViewById(R.id.last_message_time_text);
            userProfile = itemView.findViewById(R.id.userProfile);

        }
    }
}
