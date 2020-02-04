package com.digipodium.chitthi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class showImage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);
        ImageView img= findViewById(R.id.imgView);
        Intent i= getIntent();
        Bundle extras = getIntent().getExtras();
        String Url= i.getStringExtra("imgUrl");
        Glide.with(this).load(Url).fitCenter().into(img);
    }
}
