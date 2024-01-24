package com.example.digitart;

public class BluetoothPeer {
    private String name;
    private String mac;

    public BluetoothPeer(String name, String mac){
        this.name = name;
        this.mac = mac;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
