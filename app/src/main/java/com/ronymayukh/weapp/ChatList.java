package com.ronymayukh.weapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ChatList extends AppCompatActivity {


    String uid;
    FirebaseFirestore db;
    ArrayList<String> messageMetaData;
    ArrayList<ChatMetaData> chatListMeta;
    ListView messageMetaDataList;
    DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);
        uid= FirebaseAuth.getInstance().getUid();
        db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("ChatMetaData").document(uid);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle("Chat List");
        messageMetaData= new ArrayList<>();
        chatListMeta=new ArrayList<>();
        final ArrayAdapter myArrayAdapter = new ArrayAdapter(ChatList.this,android.R.layout.simple_list_item_1,messageMetaData);

        messageMetaDataList=(ListView)findViewById(R.id.chatListScrollView);
        messageMetaDataList.setAdapter(myArrayAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("MessageMetadata").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                ChatMetaData meta=snapshot.getValue(ChatMetaData.class);
                //Log.i("PROFILE",meta.getName());
                messageMetaData.add(meta.getName());
                chatListMeta.add(meta);
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        messageMetaDataList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChatMetaData chatData=chatListMeta.get(position);
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("ID",chatData.getId());
                intent.putExtra("Name",chatData.getName());
                startActivity(intent);
            }
        });


    }



    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addFriendMenu:
                startActivity(new Intent(this, AddFriend.class));
                return true;
            case R.id.friendMenu:
                startActivity(new Intent(this, Contact.class));
                return true;

            case R.id.updateProfileMenu:
                startActivity(new Intent(this, ViewOwnProfile.class));
                return true;

            case R.id.logOut:
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(this,Signup.class);
                Toast.makeText(getApplicationContext(),"Logged Out Successfully!!",Toast.LENGTH_LONG).show();
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}