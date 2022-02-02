package com.iitp.obsencemediadetector.Videos.SingleVideos;

import android.graphics.Bitmap;

public class ScannedVideoImages {
    private Bitmap bitmap;
    private float score;
    private float time;

    public ScannedVideoImages(Bitmap bitmap, float score, float time) {
        this.bitmap = bitmap;
        this.score = score;
        this.time = time;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }
}
