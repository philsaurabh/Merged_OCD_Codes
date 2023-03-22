package com.iitp.obsencemediadetector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.iitp.obsencemediadetector.Images.HiddenImages.HiddenImageActivity;
import com.iitp.obsencemediadetector.Images.ImageFolderScan.ImageGalleryFolders;
import com.iitp.obsencemediadetector.Images.SingleImageScan.SingleImageScan;
import com.iitp.obsencemediadetector.Testing.GalleryHiddenTesting;
import com.iitp.obsencemediadetector.Videos.FolderVideos.VideoGalleryFolders;
import com.iitp.obsencemediadetector.Videos.SingleVideos.SingleVideoScanning;
import com.iitp.obsencemediadetector.Videos.VideoChecker.CheckVideoGalleryFolders;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST = 1;
    private static final int IMAGE_REQUEST = 2;
    private static final int VIDEO_REQUEST = 3;
    private static final String TAG = "MainActivityTag";

    private LinearLayout singleImage, folderImage, singleVideo, folderVideo, folderVideoChecking;
    public static final String[] reqPermission = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED ||
           ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, reqPermission, PERMISSION_REQUEST);
            return;
        }
        singleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleImageScanning();
            }
        });

        folderImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ImageGalleryFolders.class));
            }
        });

        singleVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingleVideoScanning();
            }
        });

        folderVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, VideoGalleryFolders.class));
            }
        });

        folderVideoChecking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CheckVideoGalleryFolders.class));
            }
        });

    }

    private void SingleVideoScanning() {
        GetVideo();
    }

    private void GetVideo() {
//        Log.d(TAG, "GetImage: ");
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), VIDEO_REQUEST);
    }

    private void SingleImageScanning() {
        GetImage();
    }

    private void init() {
        singleImage = findViewById(R.id.main_single_image);
        singleVideo = findViewById(R.id.main_single_video);
        folderImage = findViewById(R.id.main_folder_image);
        folderVideo = findViewById(R.id.main_folder_video);
        folderVideoChecking = findViewById(R.id.main_folder_video_checking);
    }

    private void GetImage() {
//        Log.d(TAG, "GetImage: ");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode == PERMISSION_REQUEST){
            finish();
            startActivity(getIntent());
        }
        else if(resultCode==RESULT_OK && requestCode== IMAGE_REQUEST && data!=null && data.getData()!=null){
            Uri uri = data.getData();
            Intent intent = new Intent(this, SingleImageScan.class);
            intent.putExtra("ImageUri", uri.toString());
            startActivity(intent);
        }
        else if(resultCode==RESULT_OK && requestCode == VIDEO_REQUEST && data!=null && data.getData()!=null){
            Uri uri = data.getData();
            Intent intent = new Intent(this, SingleVideoScanning.class);
            intent.putExtra("VideoUri", uri.toString());
            startActivity(intent);
        }
    }

}