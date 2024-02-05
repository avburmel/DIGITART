package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import java.util.regex.*;

public class SettingsActivity extends AppCompatActivity {
    private BluetoothPeer peer;
    private int ledNum;

    String[] eyes = {"BOTH EYES", "LEFT EYE", "RIGHT EYE"};
    String[] modes = {"RISING/FALLING MODE", "FALLING/RISING MODE", "STABLE MODE", "RISING MODE", "FALLING MODE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

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
            ledNum = arguments.getInt("button");
        }

        Spinner spinnerModes = (Spinner) findViewById(R.id.spinner_mode);
        ArrayAdapter<String> adapterSpinnerModes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields_spinner, modes);
        spinnerModes.setAdapter(adapterSpinnerModes);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_choose);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Spinner spinnerEyes = (Spinner) findViewById(R.id.spinner_eye);
        ArrayAdapter<String> adapterSpinnerEyes = new ArrayAdapter<String>(this, R.layout.for_spinner, R.id.fields_spinner, eyes);
        spinnerEyes.setAdapter(adapterSpinnerEyes);

        if (ledNum == 12) {
            spinnerEyes.setEnabled(false);
            spinnerEyes.setBackgroundColor(0xF0C0C0C0);
            toolbar.setTitle("CAT_ALL");
        }
        else {
            toolbar.setTitle("CAT_" + Integer.toString(ledNum));
        }

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
                this.peer.write(this, settings.createSettingsForCatMessage());
            }
            else if (eye.matches(eyes[1])) {
                if ((ledNum < 3) || ((ledNum > 6) && (ledNum < 10)))
                    eyeLeftLedNum = ledNum * 2 + 1;
                else
                    eyeLeftLedNum = ledNum * 2;
                settings.setNum(eyeLeftLedNum);
                this.peer.write(this, settings.createSettingsMessage());
            }
            else if (eye.matches(eyes[2])) {
                if ((ledNum < 3) || ((ledNum > 6) && (ledNum < 10)))
                    eyeLeftLedNum = ledNum * 2;
                else
                    eyeLeftLedNum = ledNum * 2 + 1;
                settings.setNum(eyeLeftLedNum);
                this.peer.write(this, settings.createSettingsMessage());
            }
            this.peer.read(this);
        }
        else if (ledNum == 12)
        {
            settings.setNum(23);
            this.peer.write(this, settings.createSettingsForAllMessage());
            this.peer.read(this);
        }
        else if (ledNum == 13)
        {

        }
    }

    public void saveSettings(View v) {
        Settings settings = new Settings();
        this.peer.write(this, settings.createSaveMessage());
        this.peer.read(this);
    }

//    private int getIntFromString(String str) {
//        Pattern pattern = Pattern.compile("\\d+");
//        Matcher matcher = pattern.matcher(str);
//        int start = 0;
//        while (matcher.find(start)) {
//            String value = str.substring(matcher.start(), matcher.end());
//            int result = Integer.parseInt(value);
//            start = matcher.end();
//            return result;
//        }
//        return -1;
//    }
}