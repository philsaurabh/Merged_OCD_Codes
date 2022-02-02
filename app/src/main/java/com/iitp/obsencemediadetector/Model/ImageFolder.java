package com.iitp.obsencemediadetector.Model;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class ImageFolder {
    private  String path;
    private  String FolderName;
    private int numberOfPics = 0;
    private String firstPic;

    public ImageFolder() {
    }

    public ImageFolder(String path, String folderName, int numberOfPics, String firstPic) {
        this.path = path;
        FolderName = folderName;
        this.numberOfPics = numberOfPics;
        this.firstPic = firstPic;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return FolderName;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public int getNumberOfPics() {
        return numberOfPics;
    }

    public void setNumberOfPics(int numberOfPics) {
        this.numberOfPics = numberOfPics;
    }

    public String getFirstPic() {
        return firstPic;
    }

    public void setFirstPic(String firstPic) {
        this.firstPic = firstPic;
    }

    public void addPics(){
        this.numberOfPics++;
    }
}
