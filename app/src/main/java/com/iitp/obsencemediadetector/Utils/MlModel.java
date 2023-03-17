package com.iitp.obsencemediadetector.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.ml.Obscenetmquant;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MlModel {

    private static final String TAG = "TagMlModel";

    public static float ProcessData(Context context, Bitmap bitmap) {
        try {
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);
            TensorBuffer buffer = TensorImage.fromBitmap(bitmap).getTensorBuffer();
            Obscenetmquant model = Obscenetmquant.newInstance(context);
            Obscenetmquant.Outputs outputs = model.process(buffer);

            @NonNull TensorBuffer tensorBuffer = outputs.getOutputFeature0AsTensorBuffer();

            float a = tensorBuffer.getFloatArray()[0];
            float b = tensorBuffer.getFloatArray()[1];
            float e = 1000 * b;
            int c = (int) (e / (a + b));
            model.close();
            return (float) c / 1000;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean isVideoObscene(Context context, Uri videoUri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String time = "0";
        try {
            retriever.setDataSource(context, videoUri);
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long DurationMilliSecond = 0;
        if (time != null) {
            DurationMilliSecond = Long.parseLong(time);
        }
        Log.d(TAG, "isVideoObscene: Time: " + time);
        Log.d(TAG, "isVideoObscene: Seconds: " + DurationMilliSecond);

        int looper = 0;

        if (DurationMilliSecond <= 120 * 1000) {
            looper = 3 * 1000;
        } else if (DurationMilliSecond <= 600 * 1000) {
            looper = 4 * 1000;
        } else if (DurationMilliSecond <= 1800 * 1000) {
            looper = 15 * 1000;
        } else if (DurationMilliSecond <= 3600 * 1000) {
            looper = 30 * 1000  ;
        } else {
            looper = 60 * 1000;
        }

        long i = 0;

        while (i < DurationMilliSecond) {
            // Setting Progress Dialog

            Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                float value = MlModel.ProcessData(context, bitmap);
                if (value > 0.75) {
                    return true;
                }
            }
            i += looper;
        }
        return false;
    }

    public static ScannedVideo processVideo(Context context, Uri videoUri) {
        ScannedVideo scannedVideo = new ScannedVideo();
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String time = "0";
        try {
            retriever.setDataSource(context, videoUri);
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            e.printStackTrace();
        }

        long DurationMilliSecond = 0;
        if (time != null) {
            DurationMilliSecond = Long.parseLong(time);
        }
        Log.d(TAG, "onActivityResult: Time: " + time);
        Log.d(TAG, "onActivityResult: Seconds: " + DurationMilliSecond);

        int looper = 0;

        if (DurationMilliSecond <= 120 * 1000) {
            looper = 3 * 1000;
        } else if (DurationMilliSecond <= 600 * 1000) {
            looper = 4 * 1000;
        } else if (DurationMilliSecond <= 1800 * 1000) {
            looper = 15 * 1000;
        } else if (DurationMilliSecond <= 3600 * 1000) {
            looper = 30 * 1000  ;
        } else {
            looper = 60 * 1000;
        }

        long i = 0;
        float higherReached = -1;
        boolean taskDone = false;
        ArrayList<BitmapValue> bitmapValues = new ArrayList<>();

        while (i < DurationMilliSecond) {
            // Setting Progress Dialog

            Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                float value = MlModel.ProcessData(context, bitmap);
                if (value > 0.75) {
                    taskDone = true;
                    if (value > higherReached) {
                        higherReached = value;
                        scannedVideo = new ScannedVideo();
                        ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                        bitmapArrayList.add(bitmap);
                        scannedVideo.setBitmapArrayList(bitmapArrayList);
                        scannedVideo.setMaxValue(value);
                    }
                } else if (value >= 0.5) {
                    taskDone = true;
                    if (higherReached == -1) {
                        BitmapValue bitmapValue = new BitmapValue(bitmap, value);
                        bitmapValues.add(bitmapValue);
                        scannedVideo.setMaxValue(Math.max(scannedVideo.getMaxValue(), value));
                    }
                }
            }
            i += looper;
        }

        if(higherReached==-1){
            Collections.sort(bitmapValues, new Comparator<BitmapValue>() {
                @Override
                public int compare(BitmapValue bitmapValue1, BitmapValue bitmapValue2) {
                    return (int) (bitmapValue2.getValue()*1000-bitmapValue1.getValue()*1000);
                }
            });

            ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
            if(bitmapValues.size()>0) {
                bitmapArrayList.add(bitmapValues.get(0).getBitmap());
            }
            if(bitmapValues.size()>1) {
                bitmapArrayList.add(bitmapValues.get(1).getBitmap());
            }

            scannedVideo.setBitmapArrayList(bitmapArrayList);
        }

        if(taskDone) {
            return scannedVideo;
        }
        else{
            return null;
        }
    }
}

class BitmapValue{
    private Bitmap bitmap;
    private float value;

    public BitmapValue(Bitmap bitmap, float value) {
        this.bitmap = bitmap;
        this.value = value;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
