package com.example.digitart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BluetoothPeerAdapter extends RecyclerView.Adapter<BluetoothPeerAdapter.ViewHolder>{
    interface OnStateClickListener{
        void onStateClick(BluetoothPeer state, int position);
    }
    private final OnStateClickListener onClickListener;
    private final LayoutInflater inflater;
    private final List<BluetoothPeer> peers;

    BluetoothPeerAdapter(Context context, List<BluetoothPeer> peers, OnStateClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.peers = peers;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public BluetoothPeerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothPeerAdapter.ViewHolder holder, int position) {
        BluetoothPeer peer = peers.get(position);
        holder.macView.setText(peer.getMac());
        holder.nameView.setText(peer.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onClickListener.onStateClick(peer, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return peers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView, macView;
        ViewHolder(View view){
            super(view);
            macView = view.findViewById(R.id.mac);
            nameView = view.findViewById(R.id.name);
        }
    }
}