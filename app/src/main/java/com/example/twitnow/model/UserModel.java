package com.example.twitnow.model;

public class UserModel {
    private String fullName;
    private String username;
    private String image;
    private String email;
    private String userID;


    // Required default constructor
    public UserModel() {
        // Default constructor required for Firestore deserialization
    }


    public UserModel(String fullName, String username, String image, String email, String userID) {
        this.fullName = fullName;
        this.username = username;
        this.image = image;
        this.email = email;
        this.userID = userID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
