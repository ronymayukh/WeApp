package com.ronymayukh.weapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ViewFriendProfile extends AppCompatActivity {

    TextView number,name,status,lastSeen;
    String id;
    StorageReference storageRef;
    ImageView imageViewProfile;
    ProgressBar progressBar;
    ActionBar actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_friend_profile);
        number=findViewById(R.id.textViewNumberFriend);
        name=findViewById(R.id.textViewNameFriend);
        status=findViewById(R.id.textViewStatusFriend);
        lastSeen=findViewById(R.id.textViewLastSeen);
        imageViewProfile=findViewById(R.id.imageViewFriendView);
        progressBar=findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.VISIBLE);
        id= getIntent().getStringExtra("ID");
        loadProfile(id);
        actionBar=getSupportActionBar();

    }
    public void loadProfile(final String s){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        DocumentReference docRef = db.collection("Users").document(id);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User friend = documentSnapshot.toObject(User.class);
                name.setText(friend.getName());
                number.setText(friend.getNumber());
                status.setText("\""+friend.getStatus()+"\" ");
                lastSeen.setText(friend.getLastSeen());
                actionBar.setTitle(friend.getName());
                storageRef.child("User").child(s).child("Profile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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
        });

    }

    public void chat(View view){
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.putExtra("ID",id);
        intent.putExtra("Name",name.getText());
        startActivity(intent);
    }
}