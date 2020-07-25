package com.ronymayukh.weapp;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageListAdopter extends ArrayAdapter<Message> {
    private Activity context;
    private List<Message> messageList;

    public MessageListAdopter(Activity context, List<Message> messageList){
        super(context, R.layout.message_view,messageList);
        this.context=context;
        this.messageList=messageList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater =context.getLayoutInflater();

        View listViewItem=inflater.inflate(R.layout.message_view,null,true);

        ConstraintSet set = new ConstraintSet();

        TextView name=(TextView) listViewItem.findViewById(R.id.userTextView);
        TextView message=(TextView) listViewItem.findViewById(R.id.messageTextView);
        TextView time=(TextView) listViewItem.findViewById(R.id.timeTextView);
        ConstraintLayout constraintLayout = (ConstraintLayout)listViewItem.findViewById(R.id.messageConstrainLayout);
        RelativeLayout box=(RelativeLayout)listViewItem.findViewById(R.id.box);
        Message msg=messageList.get(position);

        RelativeLayout.MarginLayoutParams  params = (RelativeLayout.MarginLayoutParams) box.getLayoutParams();



        set.clone(constraintLayout);

        name.setText(msg.getSender());
        message.setText(msg.getText());
        time.setText(msg.getTime());
        if(msg.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            int myDynamicColor = Color.parseColor("#43464B");
            box.setBackgroundColor(myDynamicColor);
            set.setHorizontalBias(R.id.box,1);
            set.applyTo(constraintLayout);
           params.setMargins(32, 16, 24, 0);
           box.requestLayout();

        }
        else{
            int myDynamicColor = Color.parseColor("#32CD32");
           box.setBackgroundColor(myDynamicColor);
            set.setHorizontalBias(R.id.box,0);
            set.applyTo(constraintLayout);
            params.setMargins(24, 16, 32, 0);
            box.requestLayout();
        }

        return listViewItem;
    }
}
