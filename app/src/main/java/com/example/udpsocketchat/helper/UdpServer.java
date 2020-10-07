package com.example.udpsocketchat.helper;

import android.os.AsyncTask;
import android.util.Log;

import com.example.udpsocketchat.model.Event;
import com.example.udpsocketchat.model.RequestModel;
import com.example.udpsocketchat.model.ResponseModel;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UdpServer {
    static String TAG = "UDP_SOCKET";
    static Listener listener;

    public UdpServer(Listener listener) {
        this.listener = listener;
    }

    public void sendMessage(final String to, final String req) {
        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                try {

                    Log.e(TAG, "send To " + to + ", Message: " + req);

                    DatagramSocket ds = new DatagramSocket();
                    InetAddress ip = InetAddress.getByName(to);

                    DatagramPacket dp = new DatagramPacket(req.getBytes(), req.length(), ip, 3000);
                    ds.send(dp);
                    ds.close();
                } catch (IOException e) {
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onSend error");
                }
                return null;
            }
        }.execute();
    }


    public void receive() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    DatagramSocket ds = new DatagramSocket(3000);
                    byte[] buf = new byte[100000];
                    DatagramPacket dp = new DatagramPacket(buf, buf.length);
                    try {
                        ds.receive(dp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String response = new String(dp.getData(), 0, dp.getLength());
                    Log.e(TAG, "From: " + dp.getAddress() + " " + response);
                    ResponseModel responseModel = new Gson().fromJson(response, ResponseModel.class);
                    if (listener != null) {

                        if (responseModel.data != null && responseModel.data.size() > 0) {
                            Log.e("FOUND ", "ITEM " + responseModel.data.size());
                        } else {
                            Log.e("FOUND ", "ITEM0");
                        }
//                        responseModel.from_ip = dp.getAddress().getHostAddress();
                        listener.on(responseModel.event, new Gson().toJson(responseModel));
                    }

                    ds.close();
                    receive();
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "onReceive error");

                }
            }
        });
        thread.start();
    }

    public void pingToOpponent(final String subnet, final String myip, final String key) {

        new AsyncTask<Object, Object, Object>() {
            @Override
            protected Object doInBackground(Object[] objects) {
                checkHosts(subnet, myip, key);
                return null;
            }
        }.execute();
    }

    public void checkHosts(String subnet, String myip, String key) {
        try {
            for (int i = 1; i < 255; i++) {
                String ip = subnet + "." + i;
                if (!myip.equals(ip)) {
                    RequestModel requestModel = new RequestModel();
                    requestModel.event = Event.EVENT_CONNECT;
                    requestModel.from_ip = myip;
                    requestModel.uid = key;
                    sendMessage(ip, new Gson().toJson(requestModel));
                } else {
                    Log.e(TAG, "Self Ping");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
