package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    BluetoothConnectionService BTService = null;
    ServiceConnection sConn;
    private int ledNum = 0;

    String[] eyes = {"BOTH EYES", "LEFT EYE", "RIGHT EYE"};
    String[] modes = {"RISING/FALLING MODE", "RISING/FALLING MODE INV", "FALLING/RISING MODE", "FALLING/RISING MODE INV",
            "STABLE MODE", "RISING MODE", "RISING MODE INV", "FALLING MODE", "FALLING MODE INV"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Spinner spinnerModes = (Spinner) findViewById(R.id.spinner_mode);
        ArrayAdapter<String> adapterSpinnerModes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields_spinner, modes);
        spinnerModes.setAdapter(adapterSpinnerModes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_choose);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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

        Spinner spinnerEyes = (Spinner) findViewById(R.id.spinner_eye);
        ArrayAdapter<String> adapterSpinnerEyes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields_spinner, eyes);
        spinnerEyes.setAdapter(adapterSpinnerEyes);

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            ledNum = arguments.getInt("button");
        }

        if (ledNum == 12) {
            spinnerEyes.setEnabled(false);
            spinnerEyes.setBackgroundColor(0xF0C0C0C0);
            toolbar.setTitle("CAT_ALL");
        }
        else {
            toolbar.setTitle("CAT_" + Integer.toString(ledNum));
        }

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                BTService = ((BluetoothConnectionService.MyBinder) binder).getService();
            }
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService();
    }

    private void bindService() {
        Intent intent = new Intent(this, BluetoothConnectionService.class);
        bindService(intent, sConn, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(sConn);
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
        if (BTService != null) {
            int period, start, end;

            EditText periodText = (EditText) findViewById(R.id.period);
            EditText startText = (EditText) findViewById(R.id.start);
            EditText endText = (EditText) findViewById(R.id.end);
            if ((periodText.getText().length() == 0) || (startText.getText().length() == 0) || (endText.getText().length() == 0)) {
                Toast.makeText(this, "SET PARAMETERS BEFORE SENDING", Toast.LENGTH_SHORT).show();
                return;
            }
            period = Integer.parseInt(periodText.getText().toString());
            start = Integer.parseInt(startText.getText().toString());
            end = Integer.parseInt(endText.getText().toString());
            if ((period < start) || (period < end)) {
                Toast.makeText(this, "INVALID PERIOD", Toast.LENGTH_SHORT).show();
                return;
            }

            Settings settings = new Settings();
            Spinner spinnerMode = (Spinner) findViewById(R.id.spinner_mode);
            settings.setMode((String)spinnerMode.getSelectedItem());
            RXColorWheel colorPicker = (RXColorWheel) findViewById(R.id.color_wheel);
            settings.setColor(colorPicker.getColorPointerCustomColor());
            settings.setPeriod(period);
            settings.setTSStart(start);
            settings.setTSEnd(end);

            //CALC LEDNUM, CREATE AND SEND COMMAND MESSAGE
            int eyeLeftLedNum;
            Spinner spinnerEye = (Spinner) findViewById(R.id.spinner_eye);
            String eye = (String)spinnerEye.getSelectedItem();

            if (ledNum < 12)
            {
                if (eye.matches(eyes[0])) {
                    settings.setNum(ledNum);
                    BTService.write(settings.createSettingsForCatMessage());
                }
                else if (eye.matches(eyes[1])) {
                    if ((ledNum < 3) || ((ledNum > 6) && (ledNum < 10)))
                        eyeLeftLedNum = ledNum * 2 + 1;
                    else
                        eyeLeftLedNum = ledNum * 2;
                    settings.setNum(eyeLeftLedNum);
                    BTService.write(settings.createSettingsMessage());
                }
                else if (eye.matches(eyes[2])) {
                    if ((ledNum < 3) || ((ledNum > 6) && (ledNum < 10)))
                        eyeLeftLedNum = ledNum * 2;
                    else
                        eyeLeftLedNum = ledNum * 2 + 1;
                    settings.setNum(eyeLeftLedNum);
                    BTService.write(settings.createSettingsMessage());
                }
            }
            else if (ledNum == 12)
            {
                settings.setNum(23);
                BTService.write(settings.createSettingsForAllMessage());
            }
        }
    }

    public void saveSettings(View v) {
        if (BTService != null) {
            Settings settings = new Settings();
            BTService.write(settings.createSaveMessage());
        }
    }

}