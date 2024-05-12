package com.example.twitnow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitnow.R;
import com.example.twitnow.model.ChatMessageModel;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {

    Context context;
    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int i, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            holder.left_chat_layout.setVisibility(View.GONE);
            holder.right_chat_layout.setVisibility(View.VISIBLE);
            holder.right_chat_textview.setText(model.getMessage());
        } else {
            holder.left_chat_layout.setVisibility(View.VISIBLE);
            holder.right_chat_layout.setVisibility(View.GONE);
            holder.left_chat_textview.setText(model.getMessage());
        }
    }

    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_message_recycler_row, parent, false);
        return new ChatModelViewHolder(view);
    }

    class ChatModelViewHolder extends RecyclerView.ViewHolder{

        LinearLayout left_chat_layout, right_chat_layout;
        TextView left_chat_textview, right_chat_textview;


        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);

            left_chat_layout = itemView.findViewById(R.id.left_chat_layout);
            right_chat_layout = itemView.findViewById(R.id.right_chat_layout);
            left_chat_textview = itemView.findViewById(R.id.left_chat_textview);
            right_chat_textview = itemView.findViewById(R.id.right_chat_textview);

        }
    }
}
