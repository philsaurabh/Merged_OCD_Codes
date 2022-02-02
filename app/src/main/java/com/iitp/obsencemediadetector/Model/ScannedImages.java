package com.iitp.obsencemediadetector.Model;

public class ScannedImages {
    private String imagePathUri;
    private String fileName;
    private float obsceneNature;

    public ScannedImages(String imagePathUri, String fileName, float obsceneNature) {
        this.imagePathUri = imagePathUri;
        this.fileName = fileName;
        this.obsceneNature = obsceneNature;
    }

    public String getImagePathUri() {
        return imagePathUri;
    }

    public void setImagePathUri(String imagePathUri) {
        this.imagePathUri = imagePathUri;
    }

    public float getObsceneNature() {
        return obsceneNature;
    }

    public void setObsceneNature(float obsceneNature) {
        this.obsceneNature = obsceneNature;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
