package com.example.udpsocketchat.model;


import java.util.ArrayList;

public class ResponseModel {
    public String code;
    public String error;
    public String message;
    public String uid;
    public String from_ip;
    public Event event;

    public ArrayList<Data> data;
}
