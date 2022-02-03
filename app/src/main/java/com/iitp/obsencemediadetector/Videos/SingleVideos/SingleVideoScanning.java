package com.iitp.obsencemediadetector.Videos.SingleVideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.Utils.ImageResizer;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.R;

import java.util.ArrayList;

public class SingleVideoScanning extends AppCompatActivity {

    private static final String TAG = "SingleVideoTestingTag";
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    ArrayList<ScannedVideoImages> scannedVideoImagesArrayList = new ArrayList<>();
    private long start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video_testing);
        recyclerView = findViewById(R.id.video_testing_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing Video");
        progressDialog.setMessage("Please wait...");
        progressDialog.dismiss();

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("VideoUri")) {
            return;
        }

        progressDialog.show();
        start = System.currentTimeMillis();
        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                GetObsceneResult(Uri.parse(intent.getStringExtra("VideoUri")));
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                SetData();
            }
        }.execute();

    }

    private void SetData() {
        Log.d(TAG, "GetObsceneResult SetData: Size: " + scannedVideoImagesArrayList.size());
        if (scannedVideoImagesArrayList.size() == 0) {
            Toast.makeText(SingleVideoScanning.this, "No Obscenity detected in the video", Toast.LENGTH_SHORT).show();
        } else {
            SingleVideoFramesAdapter adapter = new SingleVideoFramesAdapter(this, scannedVideoImagesArrayList);
            recyclerView.setAdapter(adapter);
        }
        long end = System.currentTimeMillis();
        long duration = end - start;
        Log.d(TAG, "GetObsceneResult SetData: Time taken: " + duration);
        progressDialog.dismiss();
    }

    private void GetObsceneResult(Uri videoUri) {
        // Getting Bitmaps
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        String time = "0";
        try {
            retriever.setDataSource(this, videoUri);
            time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            Toast.makeText(SingleVideoScanning.this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
            looper = 30 * 1000;
        } else {
            looper = 60 * 1000;
        }


        long i = 0;
        scannedVideoImagesArrayList = new ArrayList<>();
        float higherReached = -1;

        while (i < DurationMilliSecond) {
            Bitmap bitmap = retriever.getFrameAtTime(i * 1000, MediaMetadataRetriever.OPTION_CLOSEST);
            if (bitmap != null) {
                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                float value = MlModel.ProcessData(this, bitmap);
                Log.d(TAG, "GetObsceneResult: Val: " + value);
                int progress = (int) (100*i);
                progress = (int) (progress/DurationMilliSecond);
                progressDialog.setMessage("Please wait... " + progress + "%");
//                if (value > 0.75) {
//                    if(value>higherReached) {
//                        higherReached = value;
//                        scannedVideoImagesArrayList.clear();
//                        ScannedVideoImages scannedVideoImages = new ScannedVideoImages(bitmap, value, (float) i / 1000);
//                        scannedVideoImagesArrayList.add(scannedVideoImages);
//                    }
//                } else
                    if (value >= 0.5) {
//                    if (higherReached==-1) {
                        ScannedVideoImages scannedVideoImages = new ScannedVideoImages(bitmap, value, (float) i / 1000);
                        scannedVideoImagesArrayList.add(scannedVideoImages);
//                    }
                }

            }

            i += looper;
        }


    }


    private void PlayVideo(Uri videoUri) {
        // PLaying Video
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, getString(R.string.app_name));
        MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(videoUri);
//            player.prepare(mediaSource);
//            player.setPlayWhenReady(true);

    }
}