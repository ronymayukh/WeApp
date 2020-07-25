package com.ronymayukh.weapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class Contact extends AppCompatActivity {

    FirebaseFirestore db;
    String uid;
    ListView friends;
    ArrayList<User> listFriend;
    ArrayList<String> listFriendId;
    ContactListAdopter myArrayAdapter;

    public User stringToUser(String s){
        int numberStart=s.indexOf("number=+")+7;
        int numberEnd=s.indexOf(", lastSeen=");

        int lastSeenStart=s.indexOf(", lastSeen=")+11;
        int lastSeenEnd=s.indexOf(", name=");

        int nameStart=s.indexOf(", name=")+7;
        int nameEnd=s.indexOf(", status=");

        int statusStart=s.indexOf(", status=")+9;
        int statusEnd=s.length();

        return new User(s.substring(nameStart,nameEnd),s.substring(numberStart,numberEnd),s.substring(statusStart,statusEnd),s.substring(lastSeenStart,lastSeenEnd));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        uid= FirebaseAuth.getInstance().getUid();


        db= FirebaseFirestore.getInstance();
        friends=findViewById(R.id.friendList);
        listFriend= new ArrayList<User>();
        listFriendId= new ArrayList<String>();
        myArrayAdapter= new ContactListAdopter(Contact.this,listFriend);
        friends.setAdapter(myArrayAdapter);
        loadContacts();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Friends");

        friends.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String friendId=listFriendId.get(position);
                Intent intent=new Intent(getApplicationContext(),ViewFriendProfile.class);
                intent.putExtra("ID",friendId);
                startActivity(intent);
            }
        });

        friends.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String friendId=listFriendId.get(position);
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("ID",friendId);
                intent.putExtra("Name",listFriend.get(position).getName());
                startActivity(intent);
                return true;
            }
        });



    }

    private void loadContacts() {
        DocumentReference docRef = db.collection("Friends").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                  Map<String, Object> contacts=documentSnapshot.getData();

                 for (Map.Entry<String,Object> friend : contacts.entrySet()){

                        listFriendId.add(friend.getKey());
                       listFriend.add(stringToUser(friend.getValue().toString()));
                       myArrayAdapter.notifyDataSetChanged();


                   }

               }
               else{
                   Toast.makeText(getApplicationContext(),"Add some Friends first",Toast.LENGTH_LONG).show();
               }
            }
        });
    }


}