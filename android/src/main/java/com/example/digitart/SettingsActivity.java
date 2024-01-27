package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

public class SettingsActivity extends AppCompatActivity {
    private BluetoothPeer peer;
    private int ledNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Bundle arguments = getIntent().getExtras();
        BluetoothDevice device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
        if (device != null) {
            peer = new BluetoothPeer(device);
            if (peer.connectBluetooth(this, peer.getDevice())) {

            }
            ledNum = arguments.getInt("button");
        }

        String[] eyes = {"BOTH EYES", "LEFT EYE", "RIGHT EYE"};
        Spinner spinnerEyes = (Spinner) findViewById(R.id.spinner_eye);
        ArrayAdapter<String> adapterSpinnerEyes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields, eyes);
        spinnerEyes.setAdapter(adapterSpinnerEyes);
        String[] modes = {"RISE/FALL", "FALL/RISE", "RISE", "FALL", "STABLE"};
        Spinner spinnerModes = (Spinner) findViewById(R.id.spinner_mode);
        ArrayAdapter<String> adapterSpinnerModes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields, modes);
        spinnerModes.setAdapter(adapterSpinnerModes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_choose);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("CAT_" + ledNum);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (peer != null)
            peer.close();
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
        Spinner spinner = (Spinner) findViewById(R.id.spinner_mode);
        settings.setMode((String)spinner.getSelectedItem());
        settings.setNum(ledNum);
        RXColorWheel colorPicker = (RXColorWheel) findViewById(R.id.color_wheel);
        settings.setColor(colorPicker.getColorPointerCustomColor());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_choose);
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