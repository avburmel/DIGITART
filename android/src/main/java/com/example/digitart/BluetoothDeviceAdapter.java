package com.example.digitart;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    interface OnDeviceClickListener{
        void onDeviceClick(BluetoothDevice device);
    }
    private final OnDeviceClickListener onClickListener;
    private final LayoutInflater inflater;
    private final ArrayList<BluetoothDevice> peers;

    BluetoothDeviceAdapter(Context context, OnDeviceClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.peers = setBluetoothDevices(context);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public BluetoothDeviceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothDeviceAdapter.ViewHolder holder, int position) {
        BluetoothDevice peer = peers.get(position);
        holder.macView.setText(peer.getAddress());
        holder.nameView.setText(peer.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onDeviceClick(peer);
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

    private ArrayList<BluetoothDevice> setBluetoothDevices(Context context) {
        ArrayList<BluetoothDevice> peers = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> devices;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        while (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED);
        devices = adapter.getBondedDevices();
        if (devices.size() > 0) {
            for (BluetoothDevice bluetoothDevice : devices) {
                if (bluetoothDevice.getName().contains("DIGITART")) {
                    peers.add(bluetoothDevice);
                }
            }
        }
        return peers;
    }
}