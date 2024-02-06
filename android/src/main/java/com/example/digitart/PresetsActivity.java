package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class PresetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

    }
}