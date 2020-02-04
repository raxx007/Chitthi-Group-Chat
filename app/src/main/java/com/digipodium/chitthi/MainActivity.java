package com.digipodium.chitthi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.digipodium.chitthi.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;


public class MainActivity extends AppCompatActivity {

    EditText textEmail,textPassword;
    ProgressBar progressBar;
    FirebaseAuth auth;
    String token;
    private DatabaseReference userRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        userRef= FirebaseDatabase.getInstance().getReference().child("User");
        FirebaseUser currentUser= auth.getCurrentUser();
        if(currentUser!=null) {
            String user = auth.getCurrentUser().toString();
            Intent i = new Intent(MainActivity.this, GroupChatActivity.class);
            startActivity(i);
        }else{
            main();
        }
}
private void main(){
    setContentView(R.layout.activity_main);
    auth = FirebaseAuth.getInstance();
    textEmail = findViewById(R.id.email_ed_login);
    textPassword = findViewById(R.id.password_ed_login);
    progressBar = findViewById(R.id.progressBarLogin);
    }

@Override
protected void onStart(){
    super.onStart();
    FirebaseUser currentUser= auth.getCurrentUser();
    if(currentUser!=null){
        Intent i= new Intent(MainActivity.this, GroupChatActivity.class);
        startActivity(i);
    }else{
        main();
    }
}

    @Override
    public void onBackPressed(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
        finish();
}
    public void Loginuser (View v){
    progressBar.setVisibility(View.VISIBLE);
     String email= textEmail.getText().toString();
     String password= textPassword.getText().toString();
     if(email.length()>1 && password.length()>1&& email.contains("@")){
         auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
             @Override
             public void onComplete(@NonNull Task<AuthResult> task) {
                 if ((task.isSuccessful())){
                     progressBar.setVisibility(View.GONE);
                     final String cuid= auth.getCurrentUser().getUid();
                      FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                         @Override
                         public void onComplete(@NonNull Task<InstanceIdResult> task) {
                             token= task.getResult().getToken();
                             userRef.child(cuid).child("device_token").setValue(token).addOnCompleteListener(new OnCompleteListener<Void>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Void> task) {
                                     if (task.isSuccessful()){
                                         Log.d("tag", "token");
                                     }
                                 }
                             });
                         }
                     });
                    //to get current user name
                     updateUI(task.getResult().getUser());
                 }
                 else{
                    Toast.makeText(MainActivity.this, "Email or Password is invalid", Toast.LENGTH_SHORT).show();
                     progressBar.setVisibility(View.GONE);
                 }
             }
         });
     }else if (email.length()<1&&password.length()<1){
         Toast.makeText(this, "You must fill all Fileds", Toast.LENGTH_SHORT).show();
         progressBar.setVisibility(View.GONE);
     } else if (email.length()>=1&&email.contains("@")&& password.length()<1){
         Toast.makeText(this, "You must fill all Fileds", Toast.LENGTH_SHORT).show();
         progressBar.setVisibility(View.GONE);
        }else if (email.length()<1&&password.length()>=1){
         Toast.makeText(this, "You must fill all Fileds", Toast.LENGTH_SHORT).show();
         progressBar.setVisibility(View.GONE);
     }else {
         Toast.makeText(this, "Invalid email", Toast.LENGTH_SHORT).show();
         progressBar.setVisibility(View.GONE);
     }

    }
    private void updateUI(FirebaseUser user) {
        if(user!=null){
            startActivity(new Intent( MainActivity.this, GroupChatActivity.class));
            finish();
        }else{

            Toast.makeText(this, "problem", Toast.LENGTH_SHORT).show();
        }
    }

    public void gotoregister(View v){

        Intent i= new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
    }

    public void forgotPassword (View v){
        AlertDialog.Builder alert= new AlertDialog.Builder(MainActivity.this);
        LinearLayout container=  new LinearLayout(MainActivity.this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams ip= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ip.setMargins(50,0,0,100);
        final EditText input = new EditText(MainActivity.this);
        input.setLayoutParams(ip);
        input.setGravity(Gravity.TOP|Gravity.START);
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setLines(1);
        input.setMaxLines(1);
        container.addView(input,ip);
        alert.setMessage("Enter Your Registered E-mail Address");
        alert.setTitle("Forgot Password");
        alert.setView(container);
        alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                String entered_email= input.getText().toString();
                auth.sendPasswordResetEmail(entered_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "A password reset link has been sent to your registered E-mail", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).show();
    }
}
