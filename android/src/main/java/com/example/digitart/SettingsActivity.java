package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private BluetoothPeer peer;
    private int ledNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            BluetoothDevice device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
            this.peer = new BluetoothPeer(device);
            if (peer.connectBluetooth(this, peer.getDevice())) {

            }
            if (arguments != null) {
                ledNum = arguments.getInt("button");
            }
        }

        String[] modes = {"RISING/FALLING MODE", "FALLING/RISING MODE", "RISING MODE", "FALLING MODE", "STABLE MODE"};
        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.modes, modes);
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

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("ledNum", this.ledNum);
        savedInstanceState.putParcelable(BluetoothDevice.class.getSimpleName(), (Parcelable) this.peer);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.ledNum = savedInstanceState.getInt("ledNum");
        this.peer = savedInstanceState.getParcelable(BluetoothDevice.class.getSimpleName());
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
        settings.setNum(ledNum);
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