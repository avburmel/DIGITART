package com.example.digitart;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class BluetoothConnectionService extends Service {
    private ConnectedThread mConnectedThread = null;
    MyBinder binder = new MyBinder();

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onRebind(Intent intent) {

    }

    @Override
    public void onDestroy() {

    }

    class MyBinder extends Binder {
        BluetoothConnectionService getService() {
            return BluetoothConnectionService.this;
        }
    }

    private static class ConnectedThread extends Thread {
        private final Queue<String> WriteQueue = new LinkedList<String>();
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean needToStop = false;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run(){
            byte[] readBuffer = new byte[1024];
            int messageLen = 0;
            boolean writeMessageSended = true;
            String writeMessage = "";
            long timeout = System.currentTimeMillis();
            while (true) {
                if (needToStop)
                    return;
                if (messageLen >= readBuffer.length)
                    messageLen = 0;
                if (writeMessageSended && !WriteQueue.isEmpty()) {
                    writeMessageSended = false;
                    writeMessage = WriteQueue.remove();
                    write(writeMessage.getBytes(StandardCharsets.US_ASCII));
                    timeout = System.currentTimeMillis();
                }
                try {
                    while (mmInStream.available() > 0) {
                        readBuffer[messageLen++] = (byte) mmInStream.read();
                        if (readBuffer[messageLen - 1] == 0) {
                            String incomingMessage = new String(readBuffer, 0, messageLen);
                            messageLen = 0;
                            if (incomingMessage.contains("OK"))
                                writeMessageSended = true;
                        }
                    }
                } catch (IOException ignored) {

                }
                if (!writeMessageSended && (timeout < System.currentTimeMillis() - 10000)) {
                    write(writeMessage.getBytes(StandardCharsets.US_ASCII));
                    timeout = System.currentTimeMillis();
                }
            }
        }

        private void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException ignored) {

            }
        }

        public void close() {
            if (mmSocket != null) {
                try {
                    mmSocket.close();
                } catch (IOException ignored) {

                }
                needToStop = true;
            }
        }

        public boolean isConnected() {
            if (mmSocket != null) {
                return mmSocket.isConnected();
            }
            return false;
        }
    }

    private void connected(BluetoothSocket mmSocket) {
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }

    public int connect(BluetoothDevice device) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothSocket mmSocket;
        try {
            mmSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        } catch (IOException e) {
            return -1;
        }
        mBluetoothAdapter.cancelDiscovery();
        try {
            mmSocket.connect();
        } catch (IOException e) {
            try {
                mmSocket.close();
            } catch (IOException ignored) {
                return -1;
            }
            return -1;
        }
        connected(mmSocket);
        return 0;
    }

    public boolean isConnected() {
        if (mConnectedThread != null)
            return mConnectedThread.isConnected();
        return false;
    }

    public void write(String message) {
        if (mConnectedThread != null)
            mConnectedThread.WriteQueue.add(message);
    }

    public void close() {
        if (mConnectedThread != null)
            mConnectedThread.close();
    }

}