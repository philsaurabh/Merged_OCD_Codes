package com.iitp.obsencemediadetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;

public class SplashActivity extends AppCompatActivity {

    ShapeableImageView iitpLogo, bprdLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        _init();

        Animation aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.zoomin);
        iitpLogo.startAnimation(aniFade);
        bprdLogo.startAnimation(aniFade);


        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        };
        handler.postDelayed(runnable, 5*1000);
    }

    private void _init() {
        iitpLogo = findViewById(R.id.splash_iitp_logo);
        bprdLogo = findViewById(R.id.splash_bprd_logo);
    }
}