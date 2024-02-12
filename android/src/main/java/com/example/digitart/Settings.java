package com.example.digitart;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.widget.Toast;

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

    String[] modes = {"STABLE MODE", "RISING MODE", "FALLING MODE", "RISING/FALLING MODE",
            "FALLING/RISING MODE", "RISING MODE INV", "FALLING MODE INV", "RISING/FALLING MODE INV", "FALLING/RISING MODE INV"};
    public Settings() {
        this.num = 23;
        this.mode = 3;
        this.color = 0x000000FF;
        this.period = 3000;
        this.TSStart = 0;
        this.TSEnd = 3000;
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
        String period = Integer.toString(this.period / 10);
        String start = Integer.toString(this.TSStart / 10);
        String end = Integer.toString(this.TSEnd / 10);
        msg = msg + "num " + num + ": smooth " + mode + ": color " + color + ": period " + period + ": TSStart " + start + ": TSEnd " + end + '\0';
        return msg;
    }

    public String createSaveMessage() {
        //"DIGITART # 1 # SAVE # "
        String msg = "DIGITART # 1 # SAVE # \0";
        return msg;
    }

    public String createSettingsForCatMessage() {
        //DIGITART # 1 # SETTINGS_FOR_CAT # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
        String msg = "DIGITART # 1 # SETTINGS_FOR_CAT # ";
        String num = Integer.toString(this.num);
        String mode = Integer.toString(this.mode);
        String color = Integer.toString(this.color);
        String period = Integer.toString(this.period / 10);
        String start = Integer.toString(this.TSStart / 10);
        String end = Integer.toString(this.TSEnd / 10);
        msg = msg + "num " + num + ": smooth " + mode + ": color " + color + ": period " + period + ": TSStart " + start + ": TSEnd " + end + '\0';
        return msg;
    }

    public String createSettingsForAllMessage() {
        //DIGITART # 1 # SETTINGS_FOR_ALL # num 0: smooth 3: color 16777215: period 400: TSStart 0: TSEnd 400
        String msg = "DIGITART # 1 # SETTINGS_FOR_ALL # ";
        String num = Integer.toString(23);
        String mode = Integer.toString(this.mode);
        String color = Integer.toString(this.color);
        String period = Integer.toString(this.period / 10);
        String start = Integer.toString(this.TSStart / 10);
        String end = Integer.toString(this.TSEnd / 10);
        msg = msg + "num " + num + ": smooth " + mode + ": color " + color + ": period " + period + ": TSStart " + start + ": TSEnd " + end + '\0';
        return msg;
    }

    public String createBrightMessage(int bright) {
        //DIGITART # 1 # BRIGHT # bright 100
        String msg = "DIGITART # 1 # BRIGHT # bright ";
        if (bright != 0)
            bright = (int) (bright * 1.28 + 10);
        msg += Integer.toString(bright) + '\0';
        return msg;
    }

    public String createSystemTimeMessage(int hour, int min, int sec) {
        //DIGITART # 1 # SYSTEM_TIME # time 0
        String msg = "DIGITART # 1 # SYSTEM_TIME # time ";
        String strTime = Integer.toString((hour << 16) | (min << 8) | (sec));
        msg = msg + strTime + '\0';
        return msg;
    }

    public String createTimeMessage(int timeFrom, int timeTo, int on) {
        //DIGITART # 1 # TIME # time 0: time 50: on 1
        String msg = "DIGITART # 1 # TIME # ";
        String strTimeFrom = Integer.toString(timeFrom);
        String strTimeTo = Integer.toString(timeTo);
        String strOn = Integer.toString(on);
        msg = msg + "time " + strTimeFrom + ": time " + strTimeTo + ": on " + strOn + '\0';
        return msg;
    }
}