package com.ronymayukh.weapp;

public class Message {
    public String sender;
    public String text;
    public String time;
    public String senderId;

    public Message(){

    }

    public Message(String sender,String text,String time,String senderId){
        this.text=text;
        this.sender=sender;
        this.time=time;
        this.senderId=senderId;
    }


    public String getSender() {
        return sender;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getSenderId(){return senderId;}

}
