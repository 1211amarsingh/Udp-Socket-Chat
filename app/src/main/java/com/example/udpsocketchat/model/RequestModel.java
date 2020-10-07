package com.example.udpsocketchat.model;


public class RequestModel {
    public String uid;
    public String from_ip;
    public String device_id;//MyApplication.getInstance().getDeviceId();
    public Event event;

    public String message;
}
