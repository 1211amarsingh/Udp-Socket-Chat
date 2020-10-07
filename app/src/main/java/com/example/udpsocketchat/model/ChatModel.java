package com.example.udpsocketchat.model;

public class ChatModel {
    public boolean meSender;
    public String message;

    public ChatModel(boolean b, String msg) {
        this.meSender = b;
        this.message = msg;
    }
}
