package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import android.app.TimePickerDialog;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonSettingsActivity extends AppCompatActivity {

    private BluetoothDevice device = null;
    private BluetoothPeer peer;
    TextView timeFrom;
    TextView timeTo;
    Calendar dateAndTime = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_common_settings);
        timeFrom = findViewById(R.id.time_from);
        timeTo = findViewById(R.id.time_to);

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
                    sendSystemTime();
                }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("DEVICE SETTINGS");
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

    public void setTime(View v) {
        setTimeOnACurrentField(getResources().getResourceEntryName(v.getId()));
    }

    public void setTimeOnACurrentField(String id) {
        TimePickerDialog.OnTimeSetListener listener = null;
        switch(id) {
            case "time_from":
                listener = t0;
                break;
            case "time_to":
                listener = t1;
                break;
            default:
                return;
        }
        new TimePickerDialog(this, listener,
                dateAndTime.get(Calendar.HOUR_OF_DAY),
                dateAndTime.get(Calendar.MINUTE), true)
                .show();
    }

    private void setInitialDateTime(int id) {
        if (id == 0) {
            timeFrom.setText(DateUtils.formatDateTime(CommonSettingsActivity.this,
                    dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }
        else if (id == 1) {
            timeTo.setText(DateUtils.formatDateTime(CommonSettingsActivity.this,
                    dateAndTime.getTimeInMillis(), DateUtils.FORMAT_SHOW_TIME));
        }
    }

    TimePickerDialog.OnTimeSetListener t0=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime(0);
        }
    };

    TimePickerDialog.OnTimeSetListener t1=new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            dateAndTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            dateAndTime.set(Calendar.MINUTE, minute);
            setInitialDateTime(1);
        }
    };

    public void sendSettings(View v) {
        String id = getResources().getResourceEntryName(v.getId());
        switch(id) {
            case "button_bright":
                sendBright();
                break;
            case "button_time":
                sendTime();
                break;
            default:
                break;
        }
    }

    private void sendBright() {
        Settings settings = new Settings();
        SeekBar brightBar = findViewById(R.id.seek_bar_bright);
        String msg = settings.createBrightMessage(brightBar.getProgress() * 10);
        this.peer.write(this, msg);
        this.peer.read(this);
    }

    private void sendSystemTime() {
        Settings settings = new Settings();
        int hour, min, sec;
        hour = Calendar.getInstance().getTime().getHours();
        min = Calendar.getInstance().getTime().getMinutes();
        sec = Calendar.getInstance().getTime().getSeconds();
        String msg = settings.createSystemTimeMessage(hour, min, sec);
        this.peer.write(this, msg);
        this.peer.read(this);
    }

    private int getTimeFromString(String str) {
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(str);
        int start = 0;
        int result = 0;
        while (matcher.find(start)) {
            String value = str.substring(matcher.start(), matcher.end());
            result = (result << 8) + Integer.parseInt(value);
            start = matcher.end();
        }
        return (result << 8);
    }

    private void sendTime() {
        Settings settings = new Settings();
        int timeON = 0, timeOFF = 0, on = 0;
        boolean needToSend = false;
        Switch timeModeOn = findViewById(R.id.switch_time_mode);
        if (timeModeOn.isChecked()) {
            TextView timeFrom = findViewById(R.id.time_from);
            TextView timeTo = findViewById(R.id.time_to);
            String inputFrom = timeFrom.getText().toString();
            String inputTo = timeTo.getText().toString();
            if ((inputFrom.length() == 5) && (inputTo.length() == 5)) {
                timeON = getTimeFromString(inputFrom);
                timeOFF = getTimeFromString(inputTo);
                on = 1;
                needToSend = true;
            }
            else {
                Toast.makeText(this, "SET TIME PARAMETERS BEFORE SENDING", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            timeON = 0;
            timeOFF = 0;
            on = 0;
            needToSend = true;
        }
        if (needToSend) {
            String msg = settings.createTimeMessage(timeON, timeOFF, on);
            this.peer.write(this, msg);
            this.peer.read(this);
        }
    }

    public void saveSettings(View v) {
        Settings settings = new Settings();
        this.peer.write(this, settings.createSaveMessage());
        this.peer.read(this);
    }

}