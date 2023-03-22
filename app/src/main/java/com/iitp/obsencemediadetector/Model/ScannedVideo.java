package com.iitp.obsencemediadetector.Model;

import android.graphics.Bitmap;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ScannedVideo {
    private ArrayList<String> bitmapArrayList;
    private float maxValue;
    private String fileName;

    public ScannedVideo() {
        this.maxValue = 0;
        this.bitmapArrayList = new ArrayList<>();
    }

    public ArrayList<String> getBitmapArrayList() {
        return bitmapArrayList;
    }

    public void setBitmapArrayList(ArrayList<String> bitmapArrayList) {
        this.bitmapArrayList = bitmapArrayList;
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "ScannedVideo{" +
                "maxValue=" + maxValue +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
