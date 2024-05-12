package com.example.twitnow.adapter;

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
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.AndroidUtil;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class SearchUserRecyclerAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserRecyclerAdapter.UserModalViewHolder> {

    Context context;
    public SearchUserRecyclerAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserModalViewHolder holder, int i, @NonNull UserModel userModel) {

        if (userModel.getImage() != null){
            AndroidUtil.setProfilePic(context, userModel.getImage(), holder.userProfile);
        }

        holder.user_fullname.setText(userModel.getFullName());
        holder.userUsername.setText(userModel.getUsername());
        if (userModel.getUserID().equals(FirebaseUtil.currentUserId())){
            holder.user_fullname.setText(userModel.getFullName()+" (Me)");
        }




        holder.itemView.setOnClickListener(v-> {
            //navigate to chat activity
            Intent intent = new Intent(context, ChatActivity.class);
            AndroidUtil.passUserModelAsIntent(intent, userModel);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public UserModalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_user_recycler_row, parent, false);
        return new UserModalViewHolder(view);
    }

    class UserModalViewHolder extends RecyclerView.ViewHolder{

        TextView user_fullname, userUsername;
        ImageView userProfile;

        public UserModalViewHolder(@NonNull View itemView) {
            super(itemView);

            user_fullname = itemView.findViewById(R.id.user_fullname);
            userUsername = itemView.findViewById(R.id.username);
            userProfile = itemView.findViewById(R.id.userProfile);

        }
    }
}
