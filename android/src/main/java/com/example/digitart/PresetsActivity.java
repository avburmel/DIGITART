package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

import java.util.ArrayList;

public class PresetsActivity extends AppCompatActivity {

    BluetoothConnectionService BTService = null;
    ServiceConnection sConn;

    ArrayList<Presets> presets = new ArrayList<Presets>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presets);

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
        PresetsAdapter.OnPresetClickListener stateClickListener = new PresetsAdapter.OnPresetClickListener() {
            @Override
            public void onPresetClick(Presets preset) {
                presetSet(preset);
            }
        };
        RecyclerView recyclerView = findViewById(R.id.presets_list);
        PresetsAdapter adapter = new PresetsAdapter(this, presets, stateClickListener);
        recyclerView.setAdapter(adapter);

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                BTService = ((BluetoothConnectionService.MyBinder) binder).getService();
            }
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService();
    }

    private void presetSet(Presets preset) {
        if (BTService != null) {
            String id = preset.getName();
            Settings settings = new Settings();
            switch(id) {
                case "VAMPIRE":
                    settings.setMode("FALLING MODE");
                    settings.setColor(preset.getColor());
                    settings.setPeriod(300);
                    settings.setTSStart(0);
                    settings.setTSEnd(300);
                    settings.setNum(23);
                    BTService.write(settings.createSettingsForAllMessage());
                    break;
                case "HALLOWEEN":
                    settings.setMode("STABLE MODE");
                    settings.setColor(preset.getColor());
                    settings.setPeriod(300);
                    settings.setTSStart(0);
                    settings.setTSEnd(300);
                    settings.setNum(23);
                    BTService.write(settings.createSettingsForAllMessage());
                    break;
                case "GHOST":
                    settings.setMode("RISING/FALLING MODE");
                    settings.setColor(preset.getColor());
                    settings.setPeriod(500);
                    settings.setTSStart(0);
                    settings.setTSEnd(500);
                    settings.setNum(23);
                    BTService.write(settings.createSettingsForAllMessage());
                    break;
                case "TICKER":
                    settings.setMode("RISING/FALLING MODE");
                    settings.setColor(preset.getColor());
                    settings.setPeriod(300);
                    for (int i = 0; i < 12; i++) {
                        settings.setTSStart(i * 25);
                        settings.setTSEnd((i * 25) + 25);
                        settings.setNum(i);
                        BTService.write(settings.createSettingsForCatMessage());
                    }
                    break;
                case "RANDOM":
                    break;
                default:
                    break;
            }
        }
    }

    private void bindService() {
        Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, sConn, 0);
    }

    private void setInitialData() {
        presets.add(new Presets ("VAMPIRE", 0xFFFF0000));
        presets.add(new Presets ("HALLOWEEN", 0xFFF28F1C));
        presets.add(new Presets ("GHOST", 0xFF3FFF89));
        presets.add(new Presets ("TICKER", 0x996750A4));
        presets.add(new Presets ("RANDOM", 0xFF000000));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sConn);
    }

}
