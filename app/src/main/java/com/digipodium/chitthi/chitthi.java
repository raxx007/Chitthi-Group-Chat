package com.digipodium.chitthi;

import android.annotation.SuppressLint;
import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class chitthi extends Application {

    public void onCreate() {

        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
