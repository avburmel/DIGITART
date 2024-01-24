package com.example.digitart;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class Settings extends AppCompatActivity {
    String[] modes = {"RISING/FALLING MODE", "FALLING/RISING MODE", "RISING MODE", "FALLING MODE", "STABLE MODE"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, modes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setTitle("ABSTRACT CATS");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                onBackPressed();
            }
        });
    }

    private void defaultSettings() {
        EditText red = (EditText) findViewById(R.id.color_red);
        red.setText("255");
        EditText green = (EditText) findViewById(R.id.color_green);
        green.setText("0");
        EditText blue = (EditText) findViewById(R.id.color_blue);
        blue.setText("0");

        EditText period = (EditText) findViewById(R.id.period);
        period.setText("500");
        EditText start = (EditText) findViewById(R.id.start);
        start.setText("0");
        EditText end = (EditText) findViewById(R.id.end);
        end.setText("500");
    }

    public void setDefaultSettings(View v) {
        defaultSettings();
    }
}