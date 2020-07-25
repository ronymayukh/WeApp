package com.ronymayukh.weapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ViewOwnProfile extends AppCompatActivity {

    FirebaseFirestore db;
    Bitmap bitmap;
    StorageReference storageRef;
    ImageView imageViewProfile;
    String uid;
    User owner;
    EditText name,status;
    TextView number;
    private static int RESULT_LOAD_IMAGE = 1;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_own_profile);
        uid= FirebaseAuth.getInstance().getUid();
        db= FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();


        name=findViewById(R.id.editTextName);
        status=findViewById(R.id.editTextStatus);
        number=findViewById(R.id.phoneNumberOwnerView);
        imageViewProfile=findViewById(R.id.imageViewProfile);

        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                owner = documentSnapshot.toObject(User.class);
                loadProfile(owner);
            }
        });
        progressBar=findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            imageViewProfile.setImageURI(selectedImage);

            BitmapDrawable drawable= (BitmapDrawable) imageViewProfile.getDrawable();
            bitmap = drawable.getBitmap();

        }
    }


    private void loadProfile(User owner) {
        name.setText(owner.getName());
        number.setText(owner.getNumber());
        status.setText(owner.getStatus());
        storageRef.child("User").child(uid).child("Profile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso. get(). load(uri.toString()).into(imageViewProfile);
                progressBar.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    public void onLoadImageUpdate(View view){
        Intent i = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    public void update(View view){
        User profile=new User(name.getText().toString(),number.getText().toString(),status.getText().toString(),"online");
        db.collection("Users").document(uid).set(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();
                    storageRef.child("User").child(uid).child("Profile Picture").putBytes(data);
                }
                catch (Exception e){

                }
                Toast.makeText(getApplicationContext(),"Profile Successfully updated",Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),ChatList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("phonenumber",number.getText().toString());
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