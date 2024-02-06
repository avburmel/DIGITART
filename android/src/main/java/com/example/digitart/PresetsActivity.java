package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;

public class PresetsActivity extends AppCompatActivity {
    ArrayList<Presets> states = new ArrayList<Presets>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        setInitialData();
        RecyclerView recyclerView = findViewById(R.id.presets_list);
        PresetsAdapter adapter = new PresetsAdapter(this, states);
        recyclerView.setAdapter(adapter);
    }
    private void setInitialData(){
        states.add(new Presets ("CATS", R.drawable.cats_tree));
    }

}
