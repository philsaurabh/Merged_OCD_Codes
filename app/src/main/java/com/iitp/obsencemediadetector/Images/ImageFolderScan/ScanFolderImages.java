package com.iitp.obsencemediadetector.Images.ImageFolderScan;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gkemon.XMLtoPDF.PdfGenerator;
import com.gkemon.XMLtoPDF.PdfGeneratorListener;
import com.gkemon.XMLtoPDF.model.FailureResponse;
import com.gkemon.XMLtoPDF.model.SuccessResponse;
import com.google.gson.Gson;
import com.iitp.obsencemediadetector.Utils.FetchIMEI;
import com.iitp.obsencemediadetector.Utils.ImageResizer;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.Model.ImageFolder;
import com.iitp.obsencemediadetector.Model.PictureFacer;
import com.iitp.obsencemediadetector.Model.ScannedImages;
import com.iitp.obsencemediadetector.R;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ScanFolderImages extends AppCompatActivity {

    private static final String TAG = "TagScanFolderImages";
    private static final int PHONE_STATE_REQUEST = 21;
    private static final float MAX_OBSCENE_SCORE = (float) 0.90;
    RecyclerView recyclerView;
    ScannedFolderImagesAdapter adapter;
    Button printButton;
    TextView imei, date, folderPath;
    LinearLayout printLl;
    ImageFolder imageFolder;

    ArrayList<ScannedImages> scannedImagesArrayList= new ArrayList<>();
    long startMilliSec, endMilliSec;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_folder_images);
//        Log.d(TAG, "onCreate: Start Time: " + System.currentTimeMillis());
        init();

        Intent intent = getIntent();
        if(intent==null || !intent.hasExtra("folderGson")){
            return;
        }

        imageFolder = new Gson().fromJson(intent.getStringExtra("folderGson"), ImageFolder.class);
        SetHeaderData();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fetching Data");
        progressDialog.show();

        new ScannedFolderImageBkgTask(this){
            @Override
            public void doInBackground() {
                super.doInBackground();
                ArrayList<PictureFacer> folderImageList = getAllImagesByFolder(imageFolder.getPath());
//                Log.d(TAG, "doInBackground: Time to fetch all photos: " + System.currentTimeMillis());
                getRcvData(folderImageList);
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                endMilliSec = System.currentTimeMillis();
                SetRecyclerView();
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
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)==PackageManager.PERMISSION_GRANTED) {
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
        Date res = new Date(startMilliSec);
        date.setText(simple.format(res));
        folderPath.setText(imageFolder.getPath());
    }

    private void init(){
        startMilliSec = System.currentTimeMillis();
        recyclerView = findViewById(R.id.scan_folder_images_rcv);
        printButton = findViewById(R.id.scan_folder_images_print);
        imei = findViewById(R.id.scan_folder_images_imei);
        date = findViewById(R.id.scan_folder_images_date);
        printLl = findViewById(R.id.scan_folder_images_print_ll);
        folderPath = findViewById(R.id.scan_folder_images_folder_path);
    }

    private void PrintPage() {
        PdfGenerator.getBuilder()
                .setContext(this)
                .fromViewSource()
                .fromView(printLl)
                .setFileName(imageFolder.getFolderName() + "_" + startMilliSec)
                .setFolderName("Image Folder Scan")
                .openPDFafterGeneration(true)
                .build(new PdfGeneratorListener() {
                    @Override
                    public void onStartPDFGeneration() {
//                        Log.d(TAG, "onStartPDFGeneration: Started");
                    }

                    @Override
                    public void onFinishPDFGeneration() {
//                        Log.d(TAG, "onFinishPDFGeneration: Finished");
                    }

                    @Override
                    public void onSuccess(SuccessResponse response) {
                        super.onSuccess(response);
//                        Log.d(TAG, "onSuccess:");
                    }

                    @Override
                    public void onFailure(FailureResponse failureResponse) {
                        super.onFailure(failureResponse);
//                        Log.d(TAG, "onFailure: " + failureResponse.getErrorMessage());
                    }
                });


    }

    private void getRcvData(ArrayList<PictureFacer> folderImageList) {
        scannedImagesArrayList = new ArrayList<>();
        int count = 0;
        int total = folderImageList.size();
        for(PictureFacer pictureFacer : folderImageList) {
            int progress = count*100;
            progress/= total;
            progressDialog.setMessage("Please wait... " + progress + "%");
            count++;
            if(pictureFacer==null){
                continue;
            }
            Uri uri = Uri.fromFile(new File(pictureFacer.getPicturePath()));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap = ImageResizer.reduceBitmapSize(bitmap, 240000);
                float index = MlModel.ProcessData(this, bitmap);
                ScannedImages scannedImages = new ScannedImages(pictureFacer.getPicturePath(), pictureFacer.getPicturName(), index);
                scannedImagesArrayList.add(scannedImages);
//                Log.d(TAG, "onCreate: Index: " + index);
            } catch (IOException e) {
//                Log.d(TAG, "onCreate: Err Catch: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void SetRecyclerView() {
        Collections.sort(scannedImagesArrayList, new Comparator<ScannedImages>() {
            @Override
            public int compare(ScannedImages scannedImages1, ScannedImages scannedImages2) {
                return (int) (scannedImages2.getObsceneNature()*1000-scannedImages1.getObsceneNature()*1000);
            }
        });

        ArrayList<ScannedImages> obsceneImageList = new ArrayList<>();
        for(ScannedImages scannedImages : scannedImagesArrayList){
            if(scannedImages.getObsceneNature()>=MAX_OBSCENE_SCORE){
                obsceneImageList.add(scannedImages);
            }
        }

//        adapter = new ScannedFolderAdapter(this, scannedImagesArrayList);
        adapter = new ScannedFolderImagesAdapter(this, obsceneImageList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        long timeDiff = endMilliSec - startMilliSec;
        progressDialog.dismiss();
        Toast.makeText(ScanFolderImages.this, scannedImagesArrayList.size() + " images scanned in " + timeDiff + " milliseconds" + "\n" +obsceneImageList.size() + " obscene Images found", Toast.LENGTH_LONG).show();
//        Log.d(TAG, "SetRecyclerView: End Time : " + System.currentTimeMillis());
    }

    public ArrayList<PictureFacer> getAllImagesByFolder(String path){
        ArrayList<PictureFacer> images = new ArrayList<>();
        Uri allImagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = { MediaStore.Images.ImageColumns.DATA ,MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE};
        Cursor cursor = this.getContentResolver().query( allImagesUri, projection, MediaStore.Images.Media.DATA + " like ? ", new String[] {"%"+path+"%"}, null);

        try {
            cursor.moveToFirst();
            do{
                PictureFacer pic = new PictureFacer();
                pic.setPicturName(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)));
                pic.setPicturePath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)));
                pic.setPictureSize(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)));
                images.add(pic);
//                Log.d(TAG, "getAllImagesByFolder: " + pic.toString());
            }while(cursor.moveToNext());
            cursor.close();
            ArrayList<PictureFacer> reSelection = new ArrayList<>();
            for(int i = images.size()-1;i > -1;i--){
                reSelection.add(images.get(i));
            }
            images = reSelection;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return images;
    }

    public class ScannedFolderImageBkgTask{
        private Activity activity;

        public ScannedFolderImageBkgTask(Activity activity) {
            this.activity = activity;
        }

        public void startBackground(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    doInBackground();
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onPostExecute();
                        }
                    });
                }
            }).start();
        }

        public void execute() {
            startBackground();
        }
        public void doInBackground() { }
        public void onPostExecute() { }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode == PHONE_STATE_REQUEST){
            SetHeaderData();
        }
    }
}