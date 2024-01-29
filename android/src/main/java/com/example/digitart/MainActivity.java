package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.view.View;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = findViewById(R.id.connectionList);

        BluetoothDeviceAdapter.OnStateClickListener stateClickListener = new BluetoothDeviceAdapter.OnStateClickListener() {
            @Override
            public void onStateClick(BluetoothDevice peer, int position) {
                openSettings(peer);
            }
        };

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.BLUETOOTH_CONNECT}, PackageManager.PERMISSION_GRANTED);
        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this, stateClickListener);
        if (adapter.getItemCount() > 0) {
            recyclerView.setAdapter(adapter);
        }
    }
    public void openSettings(BluetoothDevice peer) {
        Intent intent = new Intent(this, ImageActivity.class);
        intent.putExtra(BluetoothDevice.class.getSimpleName(), peer);
        startActivity(intent);
    }

    public void openImageActivity(View v) {
        Intent intent = new Intent(this, ImageActivity.class);
        startActivity(intent);
    }

    public void refresh(View v) {
        recreate();
    }
}