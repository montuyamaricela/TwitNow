package com.example.twitnow;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.twitnow.adapter.PostRecyclerAdapter;
import com.example.twitnow.model.PostModel;
import com.example.twitnow.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;

public class homeFragment extends Fragment {
    public homeFragment() {
        // Required empty public constructor
    }

    EditText postMessage;
    Button postBtn;
    RecyclerView recyclerView;
    PostModel postModel;
    String postID;
    PostRecyclerAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.post_recyclerView);
        postMessage = view.findViewById(R.id.post);
        postBtn = view.findViewById(R.id.post_btn);

        postID = FirebaseUtil.currentUserId();


        postBtn.setOnClickListener(v-> {
            String post_message = postMessage.getText().toString().trim();
            if (post_message.isEmpty()){
                return;
            }
            post(post_message);
        });

        setUpRecyclerView();
        return view;

    }

    void setUpRecyclerView() {
        Query query = FirebaseUtil.allPostCollectionReference().orderBy("postedOn", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<PostModel> options = new FirestoreRecyclerOptions.Builder<PostModel>()
                .setQuery(query, PostModel.class)
                .build();

        adapter = new PostRecyclerAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
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


    void post(String post){
        postModel = new PostModel(FirebaseUtil.currentUserId(), postID, post,Timestamp.now()); // Initialize postModel here

        FirebaseUtil.allPostCollectionReference().add(postModel).addOnCompleteListener(task-> {
            if (task.isSuccessful()){
                postMessage.setText("");
            }
        });

    }
}