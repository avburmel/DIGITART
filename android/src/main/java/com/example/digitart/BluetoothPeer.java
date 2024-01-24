package com.example.digitart;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Parcel;
import android.os.Parcelable;
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
                        // Unable to connect; close the socket and return.
                        Toast.makeText(context, "ERROR1:" + connectException.getMessage(), Toast.LENGTH_SHORT).show();
                        try {
                            socket.close();
                        } catch (IOException closeException) {
                            Toast.makeText(context, "ERROR2:" + connectException.getMessage(), Toast.LENGTH_SHORT).show();
                            return false;
                        }
                        return false;
                    }
                }
            } catch (IOException e) {
                Toast.makeText(context, "ERROR0:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                output = socket.getOutputStream();
            } catch (IOException e) {
                Toast.makeText(context, "ERROR4:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
            try {
                input = socket.getInputStream();
            } catch (IOException e) {
                Toast.makeText(context, "ERROR3:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void write(Context context, String message) {
        String testMsg = "DIGITART # 1 # SETTINGS # num 0: smooth 1: color 255: period 400: TSStart 0: TSEnd 400";
        try {
           // output.write(testMsg.getBytes(StandardCharsets.US_ASCII));
            output.write(message.getBytes(StandardCharsets.US_ASCII));
        } catch (IOException e) {
            Toast.makeText(context, "ERROR5:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void read(Context context) {
        byte[] buffer = new byte[1024];
        try {
            int numBytes = input.read(buffer);
            String s = new String(buffer, StandardCharsets.US_ASCII);
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "ERROR6:" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Settings settings = new Settings();
    }
}
