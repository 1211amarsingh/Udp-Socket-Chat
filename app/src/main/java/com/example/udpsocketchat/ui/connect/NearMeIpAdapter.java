package com.example.udpsocketchat.ui.connect;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.example.udpsocketchat.R;
import com.example.udpsocketchat.base.AppBaseRecycleAdapter;

import java.util.ArrayList;

public class NearMeIpAdapter extends AppBaseRecycleAdapter {

    public ArrayList<String> ip_addresslist;
    Context context;

    public NearMeIpAdapter(Context context, ArrayList<String> ip_addresslist) {
        this.ip_addresslist = ip_addresslist;
        this.context = context;
    }

    @Override
    public AppBaseRecycleAdapter.BaseViewHolder getViewHolder() {
        return new ViewHolder(inflateLayout(R.layout.item_ip_address));
    }

    @Override
    public int getDataCount() {
        return ip_addresslist != null ? ip_addresslist.size() : 0;
    }

    public void updateData(ArrayList<String> ip_addresslist) {
        this.ip_addresslist = ip_addresslist;
        notifyDataSetChanged();
    }

    private class ViewHolder extends BaseViewHolder {

        TextView tv_ip;

        public ViewHolder(View itemView) {
            super(itemView);

            tv_ip = itemView.findViewById(R.id.tv_ip);
        }


        @Override
        public void setData(int position) {
            String ip = ip_addresslist.get(position);

            tv_ip.setText(ip + "");
            tv_ip.setTag(position);
            tv_ip.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            performItemClick((Integer) v.getTag(), v);
        }
    }
}
