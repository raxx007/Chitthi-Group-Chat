package com.digipodium.chitthi.Models;

import androidx.annotation.NonNull;

public class User {

    String uid;
    String email, name;

    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    String device_token;

    public User(String uid, String email, String name, String device_token) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.device_token=device_token;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @NonNull
    @Override
    public String toString() {
        return "User{"+ "uid='" +uid + '\''+", name='" + name+ '\''+", email='" +email+'\''+'}';
    }

    public User() {


    }
}
