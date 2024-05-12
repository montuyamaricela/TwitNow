package com.example.twitnow.model;

import com.google.firebase.Timestamp;

public class PostModel {

    private String userID;
    private String postID;
    private String post;
    private Timestamp postedOn;

    public PostModel() {
    }

    public PostModel(String userID, String postID, String post, Timestamp postedOn) {
        this.userID = userID;
        this.postID = postID;
        this.post = post;
        this.postedOn = postedOn;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getPostID() {
        return postID;
    }

    public void setPostID(String postID) {
        this.postID = postID;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public Timestamp getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(Timestamp postedOn) {
        this.postedOn = postedOn;
    }
}
