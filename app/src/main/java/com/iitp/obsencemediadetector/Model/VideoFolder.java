package com.iitp.obsencemediadetector.Model;

import android.graphics.Bitmap;

public class VideoFolder {
    private String path;
    private String folderName;
    private long numOfVideos = 0;
    private String firstVideo;

    public VideoFolder() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public long getNumOfVideos() {
        return numOfVideos;
    }

    public void setNumOfVideos(long numOfVideos) {
        this.numOfVideos = numOfVideos;
    }

    public String getFirstVideo() {
        return firstVideo;
    }

    public void setFirstVideo(String firstVideo) {
        this.firstVideo = firstVideo;
    }

    public void addVideo(){
        this.numOfVideos++;
    }
}
