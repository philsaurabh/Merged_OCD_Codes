package com.iitp.obsencemediadetector;

import android.util.Log;

import com.orm.SugarApp;

public class BaseApplication extends SugarApp {
    private static final String TAG = "BaseApplicationTag";

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate: Created");
    }
}
