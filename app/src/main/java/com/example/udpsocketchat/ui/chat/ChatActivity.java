package com.example.udpsocketchat.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.udpsocketchat.model.ChatModel;
import com.example.udpsocketchat.model.Event;
import com.example.udpsocketchat.helper.Listener;
import com.example.udpsocketchat.R;
import com.example.udpsocketchat.helper.UdpServer;
import com.example.udpsocketchat.model.Data;
import com.example.udpsocketchat.model.RequestModel;
import com.example.udpsocketchat.model.ResponseModel;
import com.example.udpsocketchat.ui.connect.ConnectActivity;
import com.google.gson.Gson;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    EditText edt_send_message;
    TextView tv_clients, btn_send;
    String opponent_ip;
    RecyclerView rv_chat;
    ChatAdapter adapter;
    ArrayList<ChatModel> chatlist = new ArrayList<>();
    UdpServer udpServer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        opponent_ip = getIntent().getStringExtra("ip");
        edt_send_message = findViewById(R.id.edt_send_message);
        btn_send = findViewById(R.id.btn_send);
        tv_clients = findViewById(R.id.tv_clients);
        rv_chat = findViewById(R.id.rv_chat);
        tv_clients.setText("Opponent Ip " + opponent_ip);

        btn_send.setOnClickListener(this);
        adapter = new ChatAdapter(this, chatlist);
        rv_chat.setAdapter(adapter);
        udpServer = new UdpServer(null);
        udpServer = new UdpServer(listener);
        listen();
    }


    private void listen() {
        udpServer.receive();
    }

    Listener listener = new Listener() {
        @Override
        public void on(Event event, String response) {
            if (event.equals(Event.EVENT_MESSAGE)) {

                final ResponseModel responseModel = new Gson().fromJson(response, ResponseModel.class);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        chatlist.add(new ChatModel(false, responseModel.message));
                        adapter.updateData(chatlist);
                    }
                });
            } else {
                Log.d("Event", event + "not handled");
            }
        }
    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send) {

            String msg = edt_send_message.getText().toString();
            if (!TextUtils.isEmpty(msg)) {
                chatlist.add(new ChatModel(true, msg));
                adapter.updateData(chatlist);
                edt_send_message.setText("");

                RequestModel requestModel = new RequestModel();
                requestModel.event = Event.EVENT_MESSAGE;
                requestModel.message = msg;

                udpServer.sendMessage(opponent_ip, new Gson().toJson(requestModel));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);

        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logout) {
            finish();
            startActivity(new Intent(ChatActivity.this, ConnectActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}
