package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requests();
        RecyclerView recyclerView = findViewById(R.id.connectionList);

        BluetoothDeviceAdapter.OnDeviceClickListener stateClickListener = new BluetoothDeviceAdapter.OnDeviceClickListener() {
            @Override
            public void onDeviceClick(BluetoothDevice peer) {
                openImageActivity(peer);
            }
        };

        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this, stateClickListener);
        if (adapter.getItemCount() > 0) {
            recyclerView.setAdapter(adapter);
        }
    }

    private void requests() {
        String[] permissions = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        ActivityCompat.requestPermissions(this, permissions, 2);
    }

    public void openImageActivity(BluetoothDevice peer) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(BluetoothDevice.class.getSimpleName(), peer);
        startActivity(intent);
    }

    public void openDebug(View v) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void refresh(View v) {
        recreate();
    }
}