package com.example.digitart;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class Settings {
    private int num;
    private int mode;
    private int color;
    private int period;
    private int TSStart;
    private int TSEnd;

    String[] modes = {"STABLE MODE", "RISING MODE", "FALLING MODE", "RISING/FALLING MODE", "FALLING/RISING MODE", };

    public Settings() {
        this.num = 0;
        this.mode = 3;
        this.color = 0xFF0000;
        this.period = 500;
        this.TSStart = 0;
        this.TSEnd = 500;
    }

    public int getMode() {
        return this.mode;
    }

    public void setMode(String modeStr) {
        for (int i = 0; i < modes.length; i++) {
            if (modeStr.matches(modes[i])) {
                this.mode = i;
                return;
            }
        }
    }

    public int getNum() {
        return this.num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int color) {
        this.color = color & 0xFFFFFF;
    }

    public int getPeriod() {
        return this.period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getTSStart() {
        return this.TSStart;
    }

    public void setTSStart(int start) {
        this.TSStart = start;
    }

    public int getTSEnd() {
        return this.TSEnd;
    }

    public void setTSEnd(int end) {
        this.TSEnd = end;
    }

    public String createSettingsMessage() {
        //"DIGITART # 1 # SETTINGS # num 0: smooth 1: color 255: period 400: TSStart 0: TSEnd 400"
        String msg = "DIGITART # 1 # SETTINGS # ";
        String num = Integer.toString(this.num);
        String mode = Integer.toString(this.mode);
        String color = Integer.toString(this.color);
        String period = Integer.toString(this.period);
        String start = Integer.toString(this.TSStart);
        String end = Integer.toString(this.TSEnd);
        msg = msg + "num " + num + ": smooth " + mode + ": color " + color + ": period " + period + ": TSStart " + start + ": TSEnd " + end;
        return msg;
    }
}