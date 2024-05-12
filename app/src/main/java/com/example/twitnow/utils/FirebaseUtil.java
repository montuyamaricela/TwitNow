package com.example.twitnow.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserId(){
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser != null) {
            return currentUser.getUid();
        } else {
            // Handle the scenario where the user is not authenticated
            return null;
        }
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }


    public static CollectionReference allUserCollectionReferencec(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatRoomReference(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatRoomId);
    }


    public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }

    public static String getChatRoomId(String userId1, String userId2){
        if (userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatRoomsCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatrooms");
    }


    public static DocumentReference getOtherUserFromChatroom(List<String> userIds){
        if (userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReferencec().document(userIds.get(1));
        } else {
            return allUserCollectionReferencec().document(userIds.get(0));
        }
    }

    public static String timestampToString(Timestamp timestamp){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        return sdf.format(timestamp.toDate());
    }

    public static CollectionReference allPostCollectionReference() {
        return FirebaseFirestore.getInstance().collection("posts");
    }

    public static DocumentReference getPostReference(String postId) {
        return FirebaseFirestore.getInstance().collection("posts").document(postId);
    }
}
