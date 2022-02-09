package com.iitp.obsencemediadetector.Videos.FolderVideos;

import androidx.annotation.Nullable;
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
import com.iitp.obsencemediadetector.Images.ImageFolderScan.ScanFolderImages;
import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.Model.VideoFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.Utils.FetchIMEI;
import com.iitp.obsencemediadetector.Utils.MlModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ScanFolderVideos extends AppCompatActivity {

    private static final String TAG = "ScanFolderVideosTag";

    private static final int PHONE_STATE_REQUEST = 12;
    private long startTime;
    private ArrayList<ScannedVideo> scannedVideosArrayList = new ArrayList<>();
    ArrayList<VideoFacer> videoArrayList;
    private ProgressDialog progressDialog;
    private RecyclerView recyclerView;
    private TextView imei, date, folderPath;
    private VideoFolder videoFolder;
    private LinearLayout printLl;
    private Button printButton;
    ScanFolderVideoPartsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_folder_videos);
        _init();

        Intent intent = getIntent();
        if(intent==null || !intent.hasExtra("VideoGson")){
            return;
        }


        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Processing Folder");
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        startTime = System.currentTimeMillis();
        videoFolder = new Gson().fromJson(intent.getStringExtra("VideoGson"), VideoFolder.class);
        SetHeaderData();

        Log.d(TAG, "onCreate: Start Time: " + startTime);

        new BackgroundWork(this){
            @Override
            public void doInBackground() {
                super.doInBackground();
                videoArrayList = getVideosByFolder(videoFolder.getPath());
                ScannedVideo[] scannedVideos = new ScannedVideo[videoArrayList.size()];
                for(int i=0; i<videoArrayList.size(); ++i){
                    scannedVideos[i] = null;
                }
                adapter = new ScanFolderVideoPartsAdapter(ScanFolderVideos.this, videoArrayList, scannedVideos);
//                FetchData();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
//                SetFetchedData();
                Log.d(TAG, "onPostExecute: End Time: " + System.currentTimeMillis());
                recyclerView.setLayoutManager(new LinearLayoutManager(ScanFolderVideos.this));
                recyclerView.setAdapter(adapter);
                progressDialog.dismiss();
            }
        }.execute();

        printButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrintPage();
            }
        });
    }

    private void PrintPage() {
        PdfGenerator.getBuilder()
                .setContext(this)
                .fromViewSource()
                .fromView(printLl)
                .setFileName(videoFolder.getFolderName() + "_" + startTime)
                .setFolderName("Video Folder Scan")
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

    private void _init() {
        recyclerView = findViewById(R.id.scan_folder_videos_rcv);
        folderPath = findViewById(R.id.scan_folder_videos_folder_path);
        date = findViewById(R.id.scan_folder_videos_date);
        imei = findViewById(R.id.scan_folder_videos_imei);
        printLl = findViewById(R.id.scan_folder_videos_print_ll);
        printButton = findViewById(R.id.scan_folder_videos_print);
    }

    private void SetHeaderData() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)== PackageManager.PERMISSION_GRANTED) {
            String imeiNum = FetchIMEI.getIMEI(this);
            if(imeiNum.trim().isEmpty()){
                String ANDROID_ID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                imei.setText("Android Id: " + ANDROID_ID);
            }
            imei.setText("IMEI: " + imeiNum);
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PHONE_STATE_REQUEST);
        }
        DateFormat simple = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        Date res = new Date(startTime);
        date.setText(simple.format(res));
        folderPath.setText(videoFolder.getPath());
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

        try{
            if(cursor!=null){
                cursor.moveToNext();
            }
            do{
                VideoFacer video = new VideoFacer();
                video.setVideoName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)));
                video.setVideoPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)));
                video.setVideoSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)));
                videoArrayList.add(video);
            }while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(ScanFolderVideos.this, "Err: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally {
            cursor.close();
        }

        ArrayList<VideoFacer> reSelection = new ArrayList<>();
        for(int i=videoArrayList.size()-1; i>=0; --i){
            reSelection.add(videoArrayList.get(i));
        }
        videoArrayList = reSelection;
        return videoArrayList;
    }

    private void SetFetchedData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(scannedVideosArrayList.size()==0){
            progressDialog.dismiss();
            Toast.makeText(ScanFolderVideos.this, "No Obscene Media detected!!", Toast.LENGTH_SHORT).show();
            return;
        }
        ScannedVideoAdapter adapter = new ScannedVideoAdapter(this, scannedVideosArrayList);
        recyclerView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    private void FetchData() {
        scannedVideosArrayList = new ArrayList<>();
        int totalSize = videoArrayList.size();
        int count = 0;
        for(VideoFacer videoFacer : videoArrayList){
            int progess = count*100;
            progess/= totalSize;
            count++;
            progressDialog.setMessage("Please wait... " + progess + "%");
            String path = videoFacer.getVideoPath();
            Uri videoUri = Uri.parse(path);
            ScannedVideo result = MlModel.processVideo(this, videoUri);
            if(result!=null){
                result.setFileName(videoFacer.getVideoName());
                scannedVideosArrayList.add(result);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode == PHONE_STATE_REQUEST){
            SetHeaderData();
        }
    }
}