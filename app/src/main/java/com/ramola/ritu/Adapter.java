package com.ramola.ritu;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.viewHolder> {


    private ArrayList<Device> list = new ArrayList<>();

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent,false);
        return new viewHolder(v);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        if (!list.get(position).name.isEmpty()) {
            holder.name.setText(list.get(position).name);
        }
        if (!list.get(position).address.isEmpty()) {
            holder.address.setText(list.get(position).address);

        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        TextView name, address;

        public viewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.bluetooth_name_textField);
            address = (TextView) itemView.findViewById(R.id.bluetooth_mac_address_textField);
        }
    }

    public void add(Device d) {
        list.add(d);
        notifyItemInserted(list.size() - 1);
    }

    public ArrayList<Device> getList() {
        return list;
    }

}
