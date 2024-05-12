package com.example.twitnow.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitnow.R;
import com.example.twitnow.model.PostModel;
import com.example.twitnow.model.UserModel;
import com.example.twitnow.utils.AndroidUtil;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;


public class PostRecyclerAdapter extends FirestoreRecyclerAdapter<PostModel, PostRecyclerAdapter.PostModelViewHolder> {

    Context context;
    public PostRecyclerAdapter(@NonNull FirestoreRecyclerOptions<PostModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
//    protected void onBindViewHolder(@NonNull PostModelViewHolder holder, int i, @NonNull PostModel model) {
//
//        // get userdetails from model.userID and use it to display the images and such
//        Query query = FirebaseUtil.allUserCollectionReferencec().whereEqualTo("userID", model.getUserID());
////        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task-> {
////            if (task.isSuccessful()){
////                UserModel userModel = task.getResult().toObject(UserModel.class);
////
////                holder.fullname.setText(userModel.getFullName());
////                if (userModel != null && userModel.getImage() != null) {
////                    AndroidUtil.setProfilePic(context, userModel.getImage(), holder.profile);
////                }
////                holder.username.setText("@"+userModel.getUsername());
////                holder.post.setText(model.getPost());
////
////                holder.itemView.setOnClickListener(v -> {
////                    FirebaseUtil.allPostCollectionReference().get().addOnCompleteListener(queryDocumentSnapshots-> {
////                        int position = holder.getAdapterPosition();
////
////                        // Check if the position is valid
////                        if (position != RecyclerView.NO_POSITION) {
////                            // Get the post ID associated with the clicked item
////                            String postId = getSnapshots().getSnapshot(position).getId();
////
////                            // Now you have the post ID, you can do whatever you need with it
////                            Log.i("Clicked Post ID", postId);
////                        }
////                    });
////                });
////            }
////        });
//    }

    protected void onBindViewHolder(@NonNull PostModelViewHolder holder, int i, @NonNull PostModel model) {
        // get userdetails from model.userID and use it to display the images and such
        Query query = FirebaseUtil.allUserCollectionReferencec().whereEqualTo("userID", model.getUserID());
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if (querySnapshot != null && !querySnapshot.isEmpty()) {
                    // There should be only one document matching the userID
                    DocumentSnapshot userSnapshot = querySnapshot.getDocuments().get(0);
                    UserModel userModel = userSnapshot.toObject(UserModel.class);
                    if (userModel != null) {
                        holder.fullname.setText(userModel.getFullName());
                        if (userModel.getImage() != null) {
                            AndroidUtil.setProfilePic(context, userModel.getImage(), holder.profile);
                        }
                        holder.username.setText("@" + userModel.getUsername());
                        holder.post.setText(model.getPost());
                    }

//                    // Set onClickListener for the item
//                    holder.itemView.setOnClickListener(v -> {
//                        // You can access the post ID associated with the clicked item directly from the model
//                        String postId = model.getPostID();
//                        Log.i("Clicked Post ID", postId);
//                        // Now you have the post ID, you can perform actions such as opening the post details activity, etc.
//                    });
                }
            } else {
                Log.e("Firestore Query", "Error getting documents: ", task.getException());
            }
        });
    }


    @NonNull
    @Override
    public PostModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.post_recycler_row, parent, false);
        return new PostModelViewHolder(view);
    }

    class PostModelViewHolder extends RecyclerView.ViewHolder{

        ImageView profile;
        TextView fullname, username, post;

        public PostModelViewHolder(@NonNull View itemView) {
            super(itemView);

            profile = itemView.findViewById(R.id.userProfile);
            fullname = itemView.findViewById(R.id.user_fullname);
            username = itemView.findViewById(R.id.user_name);
            post = itemView.findViewById(R.id.post_message);
        }
    }
}
