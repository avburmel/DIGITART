package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;

public class ImageActivity extends AppCompatActivity {
    BluetoothConnectionService BTService = null;
    ServiceConnection sConn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("CHOOSE YOUR CAT");
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

        sConn = new ServiceConnection() {
            public void onServiceConnected(ComponentName name, IBinder binder) {
                BTService = ((BluetoothConnectionService.MyBinder) binder).getService();
            }
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        Bundle arguments = getIntent().getExtras();
        if (arguments != null) {
            BluetoothDevice device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
            startService(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService();
    }

    private void startService(BluetoothDevice device) {
        Intent intent = new Intent(this, BluetoothConnectionService.class);
        startService(intent.putExtra(BluetoothDevice.class.getSimpleName(), device));
        bindService(intent, sConn, 0);
    }

    private void stopService() {
        if (BTService != null)
            BTService.close();
        stopService(new Intent(this, BluetoothConnectionService.class));
    }

    public void onClickButton(View v) {
        openSettingsActivity(getResources().getResourceEntryName(v.getId()));
    }

    public void openCommonSettingsActivity(View v) {
        Intent intent = new Intent(this, CommonSettingsActivity.class);
        startActivity(intent);
    }

    public void openPresetsActivity(View v) {
        Intent intent = new Intent(this, PresetsActivity.class);
        startActivity(intent);
    }

    public void openSettingsActivity(String id) {
        int arg = 0xFFFF;
        MediaPlayer.create(this, R.raw.meow).start();
        switch(id) {
            case "button_all":
                arg = 12;
                break;
            case "button11":
                arg = 11;
                break;
            case "button10":
                arg = 10;
                break;
            case "button9":
                arg = 9;
                break;
            case "button8":
                arg = 8;
                break;
            case "button7":
                arg = 7;
                break;
            case "button6":
                arg = 6;
                break;
            case "button5":
                arg = 5;
                break;
            case "button4":
                arg = 4;
                break;
            case "button3":
                arg = 3;
                break;
            case "button2":
                arg = 2;
                break;
            case "button1":
                arg = 1;
                break;
            case "button0":
                arg = 0;
                break;
            default:
                break;
        }
        if (arg != 0xFFFF) {
            Intent intent = new Intent(this, SettingsActivity.class);
            intent.putExtra("button", arg);
            startActivity(intent);
        }
    }
}