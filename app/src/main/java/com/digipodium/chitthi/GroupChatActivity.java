package com.digipodium.chitthi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.digipodium.chitthi.Adapters.messageAdapter;
import com.digipodium.chitthi.Models.Message;
import com.digipodium.chitthi.Models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.digipodium.chitthi.Models.AllMethods;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener, messageAdapter.OnItemClickListener {
    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference messagedb;
    messageAdapter adapter;
    StorageReference storageReference;
    FirebaseStorage mstorage;
    User u;
    List<Message> messages;
    RecyclerView rvmessage;
    EditText etMessage;
    ProgressBar mProgressbar;
    ImageButton button_chatbox_send, send_image, capture_image;
    FirebaseUser user;
    public static final int REQUEST_CODE=3;
    File photoFile = null;
    public static final int CAPTURE_IMAGE_REQUEST =1;
    String mCurrentPhotoPath;
    private static final String IMAGE_DIRECTORY_NAME= "rakesh";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_group_chat);
        init();
        displayMessages(messages);
    }

    private void init() {
        auth= FirebaseAuth.getInstance();
        database= FirebaseDatabase.getInstance();
        messagedb= database.getReference("Message");
        u= new User();
        storageReference=FirebaseStorage.getInstance().getReference("uploads");
        mstorage=FirebaseStorage.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        rvmessage= findViewById(R.id.rvMessage);
        etMessage= findViewById(R.id.etMesage);
        mProgressbar= findViewById(R.id.progressBar);
        capture_image= findViewById(R.id.capture_Image);
        button_chatbox_send= findViewById(R.id.btnupload);
        button_chatbox_send.setOnClickListener(this);
        send_image= findViewById(R.id.send_image);
        send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_CODE);
            }
        });
        messages= new ArrayList<>();
        capture_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                        captureImage();
                    }else{
                        captureImage2();

                }
                /* Capture Image function for 4.4.4 and lower. Not tested for Android Version 3 and 2 */
            }
        });
    }

    private void captureImage2() {
        try{
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFile = createImageFile2();
            if (photoFile!=null){
                displayMessage(getBaseContext(), photoFile.getAbsolutePath());
                Uri photoUri= Uri.fromFile(photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_REQUEST);
            }
        }catch (Exception e){
            displayMessage(getBaseContext(), "camera is not available");
        }
    }

    private File createImageFile2() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                displayMessage(getBaseContext(), "unable to create directory");
                return null;
            }
        }
        String timeStamp = new  SimpleDateFormat("yyyyMMDD_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath()+ File.separator+"IMG_"+timeStamp+".jpg");
        return mediaFile;
    }

    private void captureImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null){
                //create the file where the file should go
                try{
                    photoFile = createImageFile();
                    if (photoFile != null){
                        Uri photoUri = FileProvider.getUriForFile(this, "com.digipodium.chitthi.fileprovider", photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                        startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
                    }
                } catch (Exception ex){
                    displayMessage(getBaseContext(), ex.getMessage().toString());
                }

            }else {
                displayMessage(getBaseContext(), "Null");
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
        String imageFileName= timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void displayMessage(Context context, String message)
    {
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult (int requestCode, String[] permissions, int[] grantResults){
        if (requestCode ==0){
            if (grantResults.length> 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                captureImage();
            }
            else {
                displayMessage(getBaseContext(), "this app is not gonna work without camera");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAPTURE_IMAGE_REQUEST){
            if ((resultCode== Activity.RESULT_OK)){
                Uri mUri= Uri.fromFile(photoFile);
                Intent i= new Intent(GroupChatActivity.this, Image_upload.class);
                i.putExtra("uri", mUri.toString());
                startActivity(i);
            }
        }
        if (requestCode==REQUEST_CODE){
            if(resultCode== Activity.RESULT_OK){
                Uri selectedImage = data.getData();
                Intent i= new Intent(GroupChatActivity.this, Image_upload.class);
                i.putExtra("uri", selectedImage.toString());
                startActivity(i);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(!TextUtils.isEmpty(etMessage.getText().toString())){
            Calendar cc = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Message message = new Message(etMessage.getText().toString(), u.getName(), sdf.format(cc.getTime()),
                    u.getEmail(), new String ("")) ;
            Log.e("color", "nothing");
            etMessage.setText("");
            messagedb.push().setValue(message);
        } else{
            Toast.makeText(this, "You cannot send blank message", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        final FirebaseUser currentUser= auth.getCurrentUser();
        u.setUid(currentUser.getUid());
        u.setEmail(currentUser.getEmail());

        database.getReference("User").child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                u= dataSnapshot.getValue(User.class);
                u.setUid(currentUser.getUid());
                AllMethods.name= u.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        messagedb.keepSynced(true);
        messagedb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message= dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                messages.add(message);
                displayMessages(messages);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message= dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                List<Message> newMessages= new ArrayList<Message>();
                for(Message m: messages){
                    if (m.getKey().equals(message.getKey())){
                        newMessages.add(message);
                    } else {
                        newMessages.add(m);
                    }
                }
                messages= newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message= dataSnapshot.getValue(Message.class);
                message.setKey(dataSnapshot.getKey());
                List<Message> newMessages= new ArrayList<Message>();
                for(Message m: messages){
                    if(!m.getKey().equals(message.getKey())){
                        newMessages.add(m);
                    }
                }
                messages= newMessages;
                displayMessages(messages);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.logoutmenu) {
            auth.signOut();
            finish();
            startActivity(new Intent(GroupChatActivity.this, MainActivity.class));
        }
        if (item.getItemId()==R.id.changePassword){
            AlertDialog.Builder alert= new AlertDialog.Builder(GroupChatActivity.this);
            LinearLayout container=  new LinearLayout(GroupChatActivity.this);
            container.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams ip= new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            ip.setMargins(50,0,0,100);
            final EditText input = new EditText(GroupChatActivity.this);
            input.setLayoutParams(ip);
            input.setGravity(Gravity.TOP|Gravity.START);
            input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
            input.setLines(1);
            input.setMaxLines(1);
            container.addView(input,ip);
            alert.setMessage("Enter New Password");
            alert.setTitle("Change Password");
            alert.setView(container);
            alert.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    String entered_password= input.getText().toString();
                    user.updatePassword(entered_password).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(GroupChatActivity.this, MainActivity.class));
                            Log.d("GC_change_password", "Password changed successfully");
                        }
                    });

                }
            }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        messages = new ArrayList<>();
    }
    @Override
    public void onBackPressed(){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    private void displayMessages(List<Message> messages) {
        LinearLayoutManager manager= new LinearLayoutManager(this);
        rvmessage.setLayoutManager(manager);
        manager.setStackFromEnd(true);
        adapter= new messageAdapter(GroupChatActivity.this, messages, messagedb);
        rvmessage.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(GroupChatActivity.this);
    }

    @Override
    public void OnItemClick(int position) {

        Intent i= new Intent(GroupChatActivity.this, showImage.class);
        Message selectedImage= messages.get(position);
        i.putExtra("imgUrl", selectedImage.getmImageURL());
        startActivity(i);
    }

    @Override
    public void OnDeleteClick(int position) {
            Message message= messages.get(position);
            if (message.getMessage().equals("")){
                Message selecteditem= messages.get(position);
                messages.remove(position);
                adapter.notifyItemRemoved(position);
                Toast.makeText(GroupChatActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                final String selectedkey= selecteditem.getKey();
                StorageReference imgref= mstorage.getReferenceFromUrl(selecteditem.getmImageURL());
                imgref.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        messagedb.child(selectedkey).removeValue();
                        adapter.notifyDataSetChanged();
                    }
                });
            }else{
                final String selectedkey= message.getKey();
                messagedb.child(selectedkey).removeValue();
                adapter.notifyDataSetChanged();
            }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case 1:
                adapter.removeItem(item.getGroupId());
                return true;

                default:
                    return super.onContextItemSelected(item);
        }

    }
}

