package com.example.twitnow.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.twitnow.model.UserModel;

public class AndroidUtil {

    public static void passUserModelAsIntent(Intent intent, UserModel model){
        intent.putExtra("username", model.getUsername());
        intent.putExtra("email", model.getEmail());
//        intent.putExtra("profile", model.getImage());
        intent.putExtra("name", model.getFullName());
        intent.putExtra("userID", model.getUserID());
        intent.putExtra("image", model.getImage());

    }

    public static UserModel getUserModelFromIntent(Intent intent){
        UserModel userModel = new UserModel();
        userModel.setUsername(intent.getStringExtra("username"));
        userModel.setEmail(intent.getStringExtra("email"));
        userModel.setFullName(intent.getStringExtra("name"));
        userModel.setUserID(intent.getStringExtra("userID"));
        userModel.setImage(intent.getStringExtra("image"));
        return userModel;
    }

    public static void setProfilePic(Context context, String imageUri, ImageView imageView){
        Glide.with(context).load(imageUri).apply(RequestOptions.circleCropTransform()).into(imageView);

    }
}
