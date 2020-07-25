package com.ronymayukh.weapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public DatabaseReference mDatabase;
    ListView messageList;
    String uid;
    String fid,fName,userName;
    ArrayList<Message> messageArrayList;
    TextView lastSeen;
    LinearLayout chatHead;
    EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uid= FirebaseAuth.getInstance().getUid();
        setContentView(R.layout.activity_main);
        fid=getIntent().getStringExtra("ID");
        fName=getIntent().getStringExtra("Name");
        messageArrayList=new ArrayList<Message>();
        ActionBar actionBar=getSupportActionBar();
        actionBar.setTitle(fName);

        lastSeen=findViewById(R.id.chatReceiverStatus);
        chatHead=findViewById(R.id.chatHead);

        FirebaseFirestore db;
        db= FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("Users").document(uid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User owner = documentSnapshot.toObject(User.class);
                userName=owner.getName();
            }
        });

        final DocumentReference docRef2 = db.collection("Users").document(fid);
        docRef2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    lastSeen.setVisibility(View.GONE);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    lastSeen.setVisibility(View.VISIBLE);
                    String lseen=snapshot.toObject(User.class).getLastSeen();
                    if(lseen.equals("online")){
                        int myDynamicColor = Color.parseColor("#32CD32");
                        chatHead.setBackgroundColor(myDynamicColor);
                        lastSeen.setText(lseen);
                    }
                    else{
                        int myDynamicColor = Color.parseColor("#C0C0C0");
                        chatHead.setBackgroundColor(myDynamicColor);
                        lastSeen.setText(lseen);
                    }

                }
            }
        });

        text=findViewById(R.id.messageEditText);
        final MessageListAdopter myArrayAdapter = new MessageListAdopter(MainActivity.this,messageArrayList);

        messageList=(ListView)findViewById(R.id.messageList);
        messageList.setAdapter(myArrayAdapter);
        mDatabase = FirebaseDatabase.getInstance().getReference();



        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message=dataSnapshot.getValue(Message.class);
                messageArrayList.add(message);
                myArrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                myArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDatabase.child("Messages").child(uid).child(fid).addChildEventListener(childEventListener);



    }

    private void writeNewMessage( String text) {
        String messageId=mDatabase.push().getKey();
        LocalDateTime now = LocalDateTime.now();
        if(text.isEmpty()){
            Toast.makeText(getApplicationContext(),"Type some message",Toast.LENGTH_SHORT).show();
            return;
        }
        ChatMetaData chatMeta=new ChatMetaData(fid,fName);
        mDatabase.child("MessageMetadata").child(uid).child(fid).setValue(chatMeta);
        ChatMetaData chatMetaF=new ChatMetaData(uid,userName);
        mDatabase.child("MessageMetadata").child(fid).child(uid).setValue(chatMetaF);

        String minutes,hours;
        int m=now.getMinute();
        if(m<10){
            minutes="0"+m;
        }
        else{
            minutes=String.valueOf(m);
        }
        int h=now.getHour();
        if(h<10){
            hours="0"+h;
        }
        else{
            hours=String.valueOf(h);
        }

        Message message = new Message(userName,text,hours+" : "+minutes,uid);


       mDatabase.child("Messages").child(uid).child(fid).child(messageId).setValue(message);
       mDatabase.child("Messages").child(fid).child(uid).child(messageId).setValue(message);
    }

    public void send(View view){

        writeNewMessage(text.getText().toString());
        text.setText("");
    }
    public void like(View view){
        writeNewMessage("❤");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewProfile:
                Intent intent=new Intent(getApplicationContext(),ViewFriendProfile.class);
                intent.putExtra("ID",fid);
                startActivity(intent);

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}