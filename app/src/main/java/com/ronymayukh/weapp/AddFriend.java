package com.ronymayukh.weapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class AddFriend extends AppCompatActivity {
    Spinner spinnerFriend;
    EditText editTextPhoneFriend;
    String uid;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid= FirebaseAuth.getInstance().getUid();
        setContentView(R.layout.activity_add_friend);
        spinnerFriend=(Spinner) findViewById(R.id.spinnerCountryFriend);
        spinnerFriend.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,CountryData.countryName));
        editTextPhoneFriend=findViewById(R.id.editTextPhoneFriend);
        progressBar=findViewById(R.id.progressBar3);
    }

    public void addFriend(View view){
        progressBar.setVisibility(View.VISIBLE);
        String code = CountryData.countryCode.get(spinnerFriend.getSelectedItemPosition());
        String phoneFriend="+" + code+editTextPhoneFriend.getText().toString();

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(phoneFriend).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                final FirebaseFirestore db;
                db= FirebaseFirestore.getInstance();

                if(!snapshot.exists()){
                    Toast.makeText(getApplicationContext(),"There is no such user with this number",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else if(snapshot.getValue().toString().equals(uid)){
                    Toast.makeText(getApplicationContext(),"This is your number!!",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
                else {

                    //writing in the friend's account
                    DocumentReference docRef2 = db.collection("Users").document(uid);
                    docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User owner = documentSnapshot.toObject(User.class);
                            Map<String, User> data = new HashMap<>();
                            data.put(uid, owner);
                            db.collection("Friends").document(snapshot.getValue().toString()).set(data, SetOptions.merge());

                        }
                    });

                    //writing in own account
                    DocumentReference docRef = db.collection("Users").document(snapshot.getValue().toString());
                    docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            User friend = documentSnapshot.toObject(User.class);
                            Map<String, User> data = new HashMap<>();
                            data.put(snapshot.getValue().toString(), friend);
                            db.collection("Friends").document(uid).set(data, SetOptions.merge());
                            Toast.makeText(getApplicationContext(), friend.getName() + " added Successfully!!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), Contact.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            progressBar.setVisibility(View.GONE);
                            startActivity(intent);


                        }
                    });
                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}