package com.digipodium.chitthi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.digipodium.chitthi.Models.Message;
import com.digipodium.chitthi.Models.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Image_upload extends AppCompatActivity {
    private ProgressBar mProgressbar;
    private FirebaseUser currentUser;
    private FirebaseAuth auth;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    ImageView img;
    Uri imageuri;
    ImageButton btnUpload;
    User u;
    private Task<Uri> mUploadTask;
    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        boolean connected= false;
        Intent intent;
        Bundle extras = getIntent().getExtras();
        imageuri = Uri.parse(extras.getString("uri"));
        img = findViewById(R.id.img);
//        Picasso.with(this).load(imageuri).into(img);
        Glide.with(this).load(imageuri).fitCenter().into(img);
        btnUpload = findViewById(R.id.btnupload);
        mProgressbar = findViewById(R.id.progressBar);
        mProgressbar.setVisibility(View.GONE);
        auth= FirebaseAuth.getInstance();
        currentUser= auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("Message");
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUploadTask!=null&&!mUploadTask.isComplete()){
                    Toast.makeText(Image_upload.this, "Upload in progress", Toast.LENGTH_SHORT).show();
                }else{
                    uploadFile();
                }

            }
        });

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void upload(){
        if (imageuri != null){
            Toast.makeText(Image_upload.this, "Upload in progress", Toast.LENGTH_SHORT).show();
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(imageuri));
            mUploadTask=fileReference.putFile(imageuri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    else {
                        return fileReference.getDownloadUrl();
                    }

                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        mProgressbar.setVisibility(View.GONE);
                        auth= FirebaseAuth.getInstance();
                        u= new User();
                        currentUser= auth.getCurrentUser();
                        u.setUid(currentUser.getUid());
                        u.setEmail(currentUser.getEmail());
                        u.setName(currentUser.getDisplayName());
                        String imagename=u.getName().toString();
                        Calendar cc = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                        Message message = new Message(new String (""), u.getName(), sdf.format(cc.getTime()),
                                u.getEmail(),downloadUri.toString() ) ;
                        databaseReference.push().setValue(message);
                        Intent i= new Intent(Image_upload.this, GroupChatActivity.class);
                        startActivity(i);
                    }
                }
            });
        }
        else
            Toast.makeText(this, "Upload failed", Toast.LENGTH_SHORT).show();
    }

    private void uploadFile() {
        ConnectivityManager connectivityManager=(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState()== NetworkInfo.State.CONNECTED
                ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
        {
            connected=true;
        } else
            connected=false;

        if (connected==false){

            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "Photo will be uploaded automatically when a connection is established", Toast.LENGTH_SHORT).show();
            upload();
        }

        else{
            mProgressbar.setVisibility(View.VISIBLE);
            upload();
        }
    }
}
