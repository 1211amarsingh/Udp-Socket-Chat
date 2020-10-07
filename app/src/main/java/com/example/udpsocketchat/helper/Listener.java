package com.example.udpsocketchat.helper;

import com.example.udpsocketchat.model.Event;

public interface Listener {

    void on(Event event, String response);
}
