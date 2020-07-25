package com.ronymayukh.weapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;

public class SetupProfile extends AppCompatActivity {

    private static int RESULT_LOAD_IMAGE = 1;
    ImageView profilePic;
    Bitmap bitmap;
    EditText name;
    String phone,uid;
    FirebaseFirestore db;
    StorageReference storageRef;
    StorageReference pic;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_profile);
        name=(EditText)findViewById(R.id.editTextName);
        uid=FirebaseAuth.getInstance().getUid();
        phone= getIntent().getStringExtra("phonenumber");
       // phone="+919836875097";

        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef= storage.getReference();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        db= FirebaseFirestore.getInstance();

        pic=storageRef.child("User").child(uid).child("Profile Picture");


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            profilePic = (ImageView) findViewById(R.id.imageViewProfilePicture);
            profilePic.setImageURI(selectedImage);

            BitmapDrawable drawable= (BitmapDrawable) profilePic.getDrawable();
            bitmap=drawable.getBitmap();

        }
    }

    public void onLoadImage(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void register(View view){
        User profile=new User(name.getText().toString(),phone,"Hey there I ma using WeApp","Online");
        db.collection("Users").document(uid).set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                mDatabase.child("Users").child(phone).setValue(uid);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();
                pic.putBytes(data);
                Intent intent=new Intent(getApplicationContext(),ChatList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phonenumber",phone);
                startActivity(intent);

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();

                    }
                });


    }


}