package com.example.digitart;

public class Presets {
    private String name;
    private int imgResource;

    public Presets(String name, int flag) {
        this.name=name;
        this.imgResource=flag;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImgResource() {
        return this.imgResource;
    }

    public void setFlagResource(int imgResource) {
        this.imgResource = imgResource;
    }
}
