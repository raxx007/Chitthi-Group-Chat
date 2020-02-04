package com.digipodium.chitthi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.digipodium.chitthi.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class RegisterActivity extends AppCompatActivity {
        EditText textEmail, textpassword, textName;
        ProgressBar progressBar;
        DatabaseReference databaseReference;
        FirebaseDatabase db;
        DatabaseReference rootRef;
        FirebaseAuth auth;
        public String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);
        textEmail= findViewById(R.id.email_ed_register);
        textpassword= findViewById(R.id.password_ed_register);
        textName= findViewById(R.id.name_ed_register);
        progressBar= findViewById(R.id.progressBarRegister);
        auth= FirebaseAuth.getInstance();
        databaseReference= FirebaseDatabase.getInstance().getReference("User");
        Button registeruser=findViewById(R.id.btnRegister);
        registeruser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                final String email= textEmail.getText().toString();
                final String password= textpassword.getText().toString();
                final String name= textName.getText().toString();

                if (email.length()>8 && password.length()>7 && email.contains("@") &&name.length() >1){

                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                final FirebaseUser firebaseUser = auth.getCurrentUser();
                                firebaseUser.sendEmailVerification();
                                FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        token= task.getResult().getToken();
                                        databaseReference.child(firebaseUser.getUid()).child("device_token").setValue(token);

                                    }
                                });

                                UserProfileChangeRequest profile= new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name).build();
                                firebaseUser.updateProfile(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Log.d("tag", name);
                                        }
                                    }
                                });

                                User u= new User();
                                u.setEmail(email);
                                u.setName(name);
                                databaseReference.child(firebaseUser.getUid()).setValue(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if ((task.isSuccessful())){
                                            Toast.makeText(RegisterActivity.this, "User Registered Successfully, Please Login", Toast.LENGTH_SHORT).show();
                                            progressBar.setVisibility(View.GONE);
                                            auth.signOut();
                                            finish();
                                            Intent i= new Intent(RegisterActivity.this, MainActivity.class);
                                            startActivity(i);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }// for all empty fields
                else if(email.length()<1 && password.length()<1 &&name.length()<1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }//for empty password , email and name not empty
                else if(email.length()>=1 && name.length()>=1 &&password.length()<1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }//for empty name , email and password not empty
                else if (email.length()>=1 && name.length()<1 &&password.length()>=1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }// for  empty email, password and name not empty
                else if(email.length()<1 && name.length()>=1 &&password.length()>=1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }//for empty email and name , password not empty
                else if (email.length()<1 && name.length()<1 &&password.length()>=1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } //for empty email and password, name not empty
                else if (email.length()<1 && name.length()>=1 &&password.length()<1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }//for empty name and password , email not empty
                else if (email.length()>=1 && name.length()<1 &&password.length()<1){
                    Toast.makeText(RegisterActivity.this, "You must fill all fields", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
                else if (password.length()<7 && email.contains("@")){
                    Toast.makeText(RegisterActivity.this, "Password must be 8 characters long", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else if (password.length()>7) {
                    Toast.makeText(RegisterActivity.this, "Invalid E-mail", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                } else {
                    Toast.makeText(RegisterActivity.this, "Invalid E-mail and password", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    public void Login(View view){
        Intent i= new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.animation_enter ,R.anim.animation_leave);
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent home_intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(home_intent);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        finish();
    }
}

