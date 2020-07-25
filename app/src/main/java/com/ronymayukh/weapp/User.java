package com.ronymayukh.weapp;

import android.graphics.Bitmap;

public class User {
    String name,number,status,lastSeen;

    public User() {
    }

    public User(String name, String number, String status, String lastSeen) {
        this.name = name;
        this.number = number;
        this.status = status;
        this.lastSeen = lastSeen;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public String getStatus() {
        return status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

}
