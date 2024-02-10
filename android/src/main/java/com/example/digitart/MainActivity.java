package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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
                openImageActivity(peer);
            }
        };

        BluetoothDeviceAdapter adapter = new BluetoothDeviceAdapter(this, stateClickListener);
        if (adapter.getItemCount() > 0) {
            recyclerView.setAdapter(adapter);
        }
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