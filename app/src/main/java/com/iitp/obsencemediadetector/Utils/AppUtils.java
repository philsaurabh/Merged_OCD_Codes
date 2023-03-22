package com.iitp.obsencemediadetector.Utils;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;

public class AppUtils {
    public static Uri Bitmap2Uri(Context context , Bitmap bitmap){
        bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("image", Context.MODE_PRIVATE);
        if (!directory.exists()) {
            directory.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File mypath = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            return Uri.fromFile(mypath);
        } catch (Exception e) {
            Log.e("SAVE_IMAGE", e.getMessage(), e);
        }
        return null;
    }

}
