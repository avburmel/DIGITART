package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class PresetsActivity extends AppCompatActivity {

    private BluetoothPeer peer;
    ArrayList<Presets> presets = new ArrayList<Presets>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            BluetoothDevice device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
            peer = new BluetoothPeer(device);
            if (device != null) {
                if (peer.connectBluetooth(this, peer.getDevice()) == false) {
                    Toast.makeText(this, "FAIL_TO_CONNECT", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    //TODO: Send time
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_presets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("PRESETS");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setInitialData();
        RecyclerView recyclerView = findViewById(R.id.presets_list);
        PresetsAdapter adapter = new PresetsAdapter(this, presets);
        recyclerView.setAdapter(adapter);
    }
    private void setInitialData() {
        presets.add(new Presets ("VAMPIRE", 0xFFFF0000));
        presets.add(new Presets ("HALLOWEEN", 0xFFFFA500));
        presets.add(new Presets ("GHOST", 0xFF30D5C8));
        presets.add(new Presets ("TICKER", 0x996750A4));
        presets.add(new Presets ("RANDOM", 0xFF000000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (peer != null)
            peer.close();
    }

}
