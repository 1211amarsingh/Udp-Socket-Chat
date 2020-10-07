package com.example.udpsocketchat.ui.connect;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;


import com.example.udpsocketchat.model.Event;
import com.example.udpsocketchat.helper.Listener;
import com.example.udpsocketchat.R;
import com.example.udpsocketchat.helper.UdpServer;
import com.example.udpsocketchat.base.ItemClickSupport;
import com.example.udpsocketchat.model.ResponseModel;
import com.example.udpsocketchat.ui.chat.ChatActivity;
import com.google.gson.Gson;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConnectActivity extends AppCompatActivity implements View.OnClickListener {
    private static final Pattern IP_ADDRESS = Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4]"
            + "[0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1]" + "[0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}"
            + "|[1-9][0-9]|[0-9]))");
    EditText et_opponent_ip;
    TextView tv_search;
    TextView tv_myip;
    NearMeIpAdapter adapter;
    ArrayList<String> ip_addresslist = new ArrayList<>();
    RecyclerView rvips;
    UdpServer udpServer;
    HashMap<String, String> map = new HashMap<>();
    Listener listener = new Listener() {
        @Override
        public void on(Event event, String response) {
            if (event.equals(Event.EVENT_CONNECT)) {
                Log.d("Pong", "" + response);
                ResponseModel responseModel = new Gson().fromJson(response, ResponseModel.class);
                map.put(responseModel.from_ip, responseModel.from_ip);
                if (ip_addresslist.size() > 0)
                    ip_addresslist.clear();
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
                    ip_addresslist.add(entry.getKey());
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(ip_addresslist);
                    }
                });
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect);

        et_opponent_ip = findViewById(R.id.et_opponent_ip);
        tv_search = findViewById(R.id.tv_search);
        tv_myip = findViewById(R.id.tv_myip);
        rvips = findViewById(R.id.rv_ips);

        tv_search.setOnClickListener(this);
        adapter = new NearMeIpAdapter(this, ip_addresslist);
        rvips.setAdapter(adapter);
        ItemClickSupport.addTo(rvips).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                et_opponent_ip.setText(ip_addresslist.get(position));
            }
        });
        udpServer = new UdpServer(listener);
    }

    String opponante_ip;
    private boolean wificonnected;
    String myip;
    String subnet;

    public boolean isWifiConnected() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wificonnected = isWifiConnected();
        if (!wificonnected) {
            Log.d("WifiStatus:", "Device Not Connected With WIFI");

            tv_myip.setText("Device Not Connected With Wifi");
        } else {
            myip = getIpv4();
            subnet = myip.substring(0, myip.lastIndexOf("."));
            if (et_opponent_ip.getText().toString().isEmpty()){
                et_opponent_ip.setText(subnet);
            }
            Log.d("WifiStatus:", "My Ip " + myip + ", Subnet " + subnet);
            tv_myip.setText("Your Ip is : " + myip);
        }
        if (subnet != null)
            pingNearDevice(subnet);
    }

    public String getIpv4() {
        String ipAddress;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        ipAddress = inetAddress.getHostAddress();
                        Log.e("ipAddress", ipAddress + "* ");
                        return ipAddress;
                    }
                }
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private void pingNearDevice(String subnet) {
        udpServer.receive();
        udpServer.pingToOpponent(subnet, myip, "");
    }


    private void onSearch() {
        opponante_ip = et_opponent_ip.getText().toString();
        Matcher matcher = IP_ADDRESS.matcher(opponante_ip);

        if (!wificonnected) {
            showToast("Device Not Connected to Wifi");
        } else if (TextUtils.isEmpty(myip)) {
            showToast("Device Ip Not Found");
        } else if (TextUtils.isEmpty(opponante_ip)) {
            showToast("Opponent Ip Not Found");
        } else if (!matcher.matches()) {
            et_opponent_ip.setError("Invalid Ip");
        } else if (!opponante_ip.startsWith(subnet)) {
            et_opponent_ip.setError("Ip not in your network");
        }  else if (opponante_ip.equals(myip)) {
            et_opponent_ip.setError("Opponent ip and your ip same not allowed");
        } else {
            listener = null;
            Intent i = new Intent(this, ChatActivity.class);
            i.putExtra("ip", opponante_ip);
            startActivity(i);
            finish();
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_search) {
            onSearch();
        }
    }
}