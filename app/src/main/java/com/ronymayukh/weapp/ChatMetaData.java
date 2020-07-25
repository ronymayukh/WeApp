package com.ronymayukh.weapp;


public class ChatMetaData {

    String id;
    String name;

    public ChatMetaData() {
    }

    public ChatMetaData(String id,String name) {
        this.id = id;
        this.name = name;
    }


    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
