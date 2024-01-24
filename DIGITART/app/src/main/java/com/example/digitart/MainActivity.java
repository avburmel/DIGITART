package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.view.View;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    ArrayList<BluetoothPeer> peers = new ArrayList<BluetoothPeer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = findViewById(R.id.connectionList);

        Intent intent = new Intent(this, Settings.class);
        BluetoothPeerAdapter.OnStateClickListener stateClickListener = new BluetoothPeerAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(BluetoothPeer state, int position) {
                startActivity(intent);
            }
        };

        if (getBluetoothIsConn() == true) {
            BluetoothPeerAdapter adapter = new BluetoothPeerAdapter(this, peers, stateClickListener);
            recyclerView.setAdapter(adapter);
        }


    }

    public void openSettings(View v) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }

    public boolean getBluetoothIsConn() {
        Set<BluetoothDevice> devices;
        Button button = (Button) findViewById(R.id.button);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, PackageManager.PERMISSION_GRANTED);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
            if (adapter.getBondedDevices().size() > 0) {
                devices = adapter.getBondedDevices();
                for (BluetoothDevice bluetoothDevice : devices) {
                    peers.add(new BluetoothPeer(bluetoothDevice.getName(), bluetoothDevice.getAddress()));
                }
                return true;
            }
        }
        return false;
    }
}