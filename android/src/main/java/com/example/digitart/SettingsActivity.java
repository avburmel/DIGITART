package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {
    private BluetoothPeer peer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            BluetoothDevice device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
            this.peer = new BluetoothPeer(device);
            if (peer.connectBluetooth(this, peer.getDevice()) == true) {

            }
        }

        String[] modes = {"RISING/FALLING MODE", "FALLING/RISING MODE", "RISING MODE", "FALLING MODE", "STABLE MODE"};

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.modes, modes);
        //ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, modes);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("CATS_TREE");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    private void defaultSettings() {
        Settings settings = new Settings();
        RXColorWheel colorPicker = findViewById(R.id.color_wheel);
        colorPicker.setColor();
        EditText period = (EditText) findViewById(R.id.period);
        period.setText(Integer.toString(settings.getPeriod()));
        EditText start = (EditText) findViewById(R.id.start);
        start.setText(Integer.toString(settings.getTSStart()));
        EditText end = (EditText) findViewById(R.id.end);
        end.setText(Integer.toString(settings.getTSEnd()));
    }

    public void setDefaultSettings(View v) {
        defaultSettings();
    }

    public void sendSettings(View v) {
        Settings settings = new Settings();
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        settings.setMode((String)spinner.getSelectedItem());
        RXColorWheel colorPicker = (RXColorWheel) findViewById(R.id.color_wheel);
        settings.setColor(colorPicker.getColorPointerCustomColor());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(Integer.toString(colorPicker.getColorPointerCustomColor()));
        EditText period = (EditText) findViewById(R.id.period);
        settings.setPeriod(Integer.parseInt(period.getText().toString()));
        EditText start = (EditText) findViewById(R.id.start);
        settings.setTSStart(Integer.parseInt(start.getText().toString()));
        EditText end = (EditText) findViewById(R.id.end);
        settings.setTSEnd(Integer.parseInt(end.getText().toString()));


        this.peer.write(this, settings.createSettingsMessage());
        this.peer.read(this);
    }
}