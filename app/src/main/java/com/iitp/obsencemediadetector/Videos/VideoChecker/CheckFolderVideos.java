package com.iitp.obsencemediadetector.Videos.VideoChecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.gson.Gson;
import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.Model.VideoFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.Utils.FetchIMEI;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckFolderVideos extends AppCompatActivity {

    private static final int PHONE_STATE_REQUEST = 12;
    private static final String TAG = "TagCheckFOlderVideos";
    private long startTime;
    private ArrayList<ScannedVideo> scannedVideosArrayList = new ArrayList<>();
    ArrayList<VideoFacer> videoArrayList;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView imei, date, folderPath;
    private VideoFolder videoFolder;
    private LinearLayout printLl;
    private Button printButton;
    private CheckFolderVideoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_folder_videos);

        Log.d(TAG, "onCreate: Start Time: " + System.currentTimeMillis());

        _init();

        Intent intent = getIntent();
        if (intent == null || !intent.hasExtra("VideoGson")) {
            return;
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing Folder");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        startTime = System.currentTimeMillis();
        videoFolder = new Gson().fromJson(intent.getStringExtra("VideoGson"), VideoFolder.class);
        SetHeaderData();

        new BackgroundWork(this) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                videoArrayList = getVideosByFolder(videoFolder.getPath());
                adapter = new CheckFolderVideoAdapter(CheckFolderVideos.this, videoArrayList);
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                recyclerView.setLayoutManager(new LinearLayoutManager(CheckFolderVideos.this));
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
                Log.d(TAG, "onPostExecute: End time: " + System.currentTimeMillis());
            }
        }.execute();

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintPage();
            }
        });
    }

    private void SetHeaderData() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            String imeiNum = FetchIMEI.getIMEI(this);
            if (imeiNum.trim().isEmpty()) {
                String ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                imei.setText("Android Id: " + ANDROID_ID);
            }
            imei.setText("IMEI: " + imeiNum);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_REQUEST);
        }
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date res = new Date(startTime);
        date.setText(simple.format(res));
        folderPath.setText(videoFolder.getPath());
    }

    private void _init() {
        recyclerView = findViewById(R.id.check_scan_folder_videos_rcv);
        folderPath = findViewById(R.id.check_scan_folder_videos_folder_path);
        date = findViewById(R.id.check_scan_folder_videos_date);
        imei = findViewById(R.id.check_scan_folder_videos_imei);
        printLl = findViewById(R.id.check_scan_folder_videos_print_ll);
        printButton = findViewById(R.id.check_scan_folder_videos_print);
    }

    private void PrintPage() {
        PdfGenerator.getBuilder()
                .setContext(this)
                .fromViewSource()
                .fromView(printLl)
                .setFileName(videoFolder.getFolderName() + "_" + startTime)
                .setFolderName("Video Folder Checking")
                .openPDFafterGeneration(true)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onStartPDFGeneration() {
                    }

                    @Override
                    public void onFinishPDFGeneration() {
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
                    }

                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
                    }
                });


    }

    private ArrayList<VideoFacer> getVideosByFolder(String path) {
        ArrayList<VideoFacer> videoArrayList = new ArrayList<>();
        Uri allVideosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[]{
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.SIZE
        };

        Cursor cursor = getContentResolver().query(allVideosUri, projection, MediaStore.Video.Media.DATA + " like ? ", new String[]{"%" + path + "%"}, null);

        try {
            if (cursor != null) {
                cursor.moveToNext();
            }
            do {
                VideoFacer video = new VideoFacer();
                video.setVideoName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                video.setVideoSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                videoArrayList.add(video);
            } while (cursor.moveToNext());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CheckFolderVideos.this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
        }

        ArrayList<VideoFacer> reSelection = new ArrayList<>();
        for (int i = videoArrayList.size() - 1; i >= 0; --i) {
            reSelection.add(videoArrayList.get(i));
        }
        videoArrayList = reSelection;
        return videoArrayList;
    }

}