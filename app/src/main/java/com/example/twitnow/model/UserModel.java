package com.example.twitnow.model;

public class UserModel {
    private String fullName;
    private String username;
    private String image;


    // Required default constructor
    public UserModel() {
        // Default constructor required for Firestore deserialization
    }


    public UserModel(String fullName, String username, String image) {
        this.fullName = fullName;
        this.username = username;
        this.image = image;
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
