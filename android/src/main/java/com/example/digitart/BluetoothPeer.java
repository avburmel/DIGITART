package com.example.digitart;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BluetoothPeer {
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private InputStream input;
    private OutputStream output;
    public BluetoothPeer(BluetoothDevice device) {
        this.device = device;
        this.socket = null;
        this.input = null;
        this.output = null;
    }

    public String getName() {
        return this.device.getName();
    }

    public String getMac() {
        return this.device.getAddress();
    }

    public BluetoothDevice getDevice() { return this.device; }

    public boolean connectBluetooth(Context context, BluetoothDevice device) {
        if (socket == null || !socket.isConnected()) {
            try {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    socket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    try {
                        socket.connect();
                    } catch (IOException connectException) {
                        return  false;
                    }
                }
            } catch (IOException e) {
                return false;
            }
            try {
                output = socket.getOutputStream();
            } catch (IOException e) {
                return false;
            }
            try {
                input = socket.getInputStream();
            } catch (IOException e) {
                return false;
            }
        }
        return true;
    }

    public void write(Context context, String message) {
        //String testMsg = "DIGITART # 1 # SETTINGS # num 0: smooth 1: color 255: period 400: TSStart 0: TSEnd 400";
        if (socket == null || !socket.isConnected())
            return;
        try {
           // output.write(testMsg.getBytes(StandardCharsets.US_ASCII));
            output.write(message.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {

        }
    }

    public void read(Context context) {
        byte[] buffer = new byte[1024];
        if (socket == null || !socket.isConnected())
            return;
        try {
            int numBytes = input.read(buffer);
            String s = new String(buffer, StandardCharsets.US_ASCII);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {

        }
    }

    public void close() {
        if (socket == null || !socket.isConnected())
            return;
        try {
            socket.close();
        } catch (IOException e) {

        }
    }
}
