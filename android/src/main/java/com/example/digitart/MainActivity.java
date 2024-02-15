package com.example.digitart;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
        else {
            TextView textView = findViewById(R.id.textView);
            textView.setText("NO BONDED DIGITART DEVICES");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void requests() {
        //REQUEST PERMISSIONS
        String[] permissions = {Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN};
        ActivityCompat.requestPermissions(this, permissions, 2);
        while (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED);
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