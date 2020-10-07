package com.example.udpsocketchat.ui.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.example.udpsocketchat.R;
import com.example.udpsocketchat.model.ChatModel;

import java.util.ArrayList;

public class ChatAdapter extends Adapter<ChatAdapter.ViewHolder> {

    public ArrayList<ChatModel> ip_addresslist;
    Context context;
    LayoutInflater inflateLayout;

    public ChatAdapter(Context context, ArrayList<ChatModel> ip_addresslist) {
        this.ip_addresslist = ip_addresslist;
        this.context = context;
        inflateLayout = LayoutInflater.from(context);
    }

    public void updateData(ArrayList<ChatModel> ip_addresslist) {
        this.ip_addresslist = ip_addresslist;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 1) {
            return new ViewHolder(inflateLayout.inflate(R.layout.item_chat_send, parent, false));
        } else {
            return new ViewHolder(inflateLayout.inflate(R.layout.item_chat, parent, false));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ip_addresslist.get(position).meSender ? 1 : 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatModel chatModel = ip_addresslist.get(position);

        holder.tv_ip.setText(chatModel.message + "");
    }


    @Override
    public int getItemCount() {
        return ip_addresslist != null ? ip_addresslist.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_ip;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_ip = itemView.findViewById(R.id.tv_ip);
        }
    }
}
