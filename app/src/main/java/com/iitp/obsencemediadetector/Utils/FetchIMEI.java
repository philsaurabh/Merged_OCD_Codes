package com.iitp.obsencemediadetector.Utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class FetchIMEI {

    @SuppressLint("HardwareIds")
    public static String getIMEI(Context context) {

        try {
            String deviceUniqueIdentifier = null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm!=null) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "No";
                }

                deviceUniqueIdentifier = tm.getDeviceId();
                if (deviceUniqueIdentifier == null || deviceUniqueIdentifier.length()==0)
                    deviceUniqueIdentifier = "Null";
            }
            return deviceUniqueIdentifier;
        }  catch (Exception e){
            Log.d("Didn't find IMEI", String.valueOf(e));
            return "";
        }
    }
}