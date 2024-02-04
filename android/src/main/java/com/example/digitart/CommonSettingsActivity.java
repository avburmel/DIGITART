package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

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
                    //TODO: Send time
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
}