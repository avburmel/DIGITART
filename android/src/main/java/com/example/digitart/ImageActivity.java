package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class ImageActivity extends AppCompatActivity {
    private BluetoothDevice device = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle arguments = getIntent().getExtras();

        if (arguments != null) {
            device = arguments.getParcelable(BluetoothDevice.class.getSimpleName());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_image);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("CHOOSE_YOUR_CAT");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putParcelable(BluetoothDevice.class.getSimpleName(), device);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        device = savedInstanceState.getParcelable(BluetoothDevice.class.getSimpleName());
    }

    public void onClickButton(View v) {
        openSettingsActivity(getResources().getResourceEntryName(v.getId()));
    }

    public void openCommonSettingsActivity(View v) {
        Intent intent = new Intent(this, CommonSettingsActivity.class);
        intent.putExtra(BluetoothDevice.class.getSimpleName(), device);
        startActivity(intent);
    }

    public void openSettingsActivity(String id) {
        int arg = 0xFFFF;
        MediaPlayer.create(this, R.raw.meow).start();
        switch(id) {
            case "button_presets":
                arg = 13;
                break;
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
            intent.putExtra(BluetoothDevice.class.getSimpleName(), device);
            intent.putExtra("button", arg);
            startActivity(intent);
        }
    }
}