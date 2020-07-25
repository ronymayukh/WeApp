package com.ronymayukh.weapp;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ContactListAdopter extends ArrayAdapter<User> {
    private Activity context;
    private List<User> contactList;

    public ContactListAdopter(Activity context, List<User> contactList) {
        super(context, R.layout.contact_view,contactList);
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();
        View listViewItem=inflater.inflate(R.layout.contact_view,null,true);

        TextView name=listViewItem.findViewById(R.id.textViewNameContact);
        final ImageView pic=listViewItem.findViewById(R.id.imageViewContact);

        User currentContact=contactList.get(position);

        //set name
        name.setText(currentContact.getName());
        //set pic

        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Users").child(currentContact.getNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageRef = storage.getReference();
                storageRef.child("User").child(snapshot.getValue().toString()).child("Profile Picture").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        // Got the download URL for 'users/me/profile.png'
                        Picasso. get(). load(uri.toString()).into(pic);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        return listViewItem;
    }
}
