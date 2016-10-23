package com.example.admin.educhat.utils;


public class Partner {

    public String uid;
    public String name;
    public Message lastmessage;

    public Message getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(Message lastmessage) {
        this.lastmessage = lastmessage;
    }
}
