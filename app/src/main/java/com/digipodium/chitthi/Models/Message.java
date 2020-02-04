package com.digipodium.chitthi.Models;

import androidx.annotation.NonNull;

public class Message {

    public Message() {
    }

    String message;
    String name;
    String key;
    String  createdAt;
    String email;

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    String mImageURL;

    public Message(String message, String name, String createdAt, String email, String mImageURL) {
        this.message = message;
        this.name = name;
        this.createdAt=createdAt;
        this.email=email;
        this.mImageURL=mImageURL;
    }

    public String getEmail() {
        return email;
    }
    public String getMessage() {
        return message;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
    public String getCreatedAt() {
        return createdAt;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }


    @NonNull
    @Override
    public String toString() {
       return "Message{"+ "Message='" +message + '\''+", name='" + name+ '\''+", key='" +key+'\''+'}';
    }
}
