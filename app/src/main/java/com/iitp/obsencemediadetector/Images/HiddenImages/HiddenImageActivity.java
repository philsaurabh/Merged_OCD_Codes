package com.iitp.obsencemediadetector.Images.HiddenImages;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.iitp.obsencemediadetector.Images.HiddenImages.CustomFile;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;

import java.io.File;
import java.util.ArrayList;

public class HiddenImageActivity extends AppCompatActivity {

    private static final String TAG = "HiddenImageActivityTag";
    Button hiddenImagesScanBtn;
    ArrayList<CustomFile> hiddenFiles = new ArrayList<>();
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hidden_image);
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Scanning images");
        hiddenImagesScanBtn = findViewById(R.id.hidden_images_btn);

        hiddenImagesScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                new BackgroundWork(HiddenImageActivity.this){
                    @Override
                    public void doInBackground() {
                        super.doInBackground();
                        hiddenFiles = filterFiles(HiddenImageActivity.this);
                    }

                    @Override
                    public void onPostExecute() {
                        super.onPostExecute();
//                        Log.d(TAG, "onPostExecute: Size: " + hiddenFiles.size());
                        PrintFileList();
                        progressDialog.dismiss();
                    }
                }.execute();
            }
        });
    }

    private void PrintFileList() {
        for(CustomFile customFile : hiddenFiles){
//            Log.d(TAG, "File: " + customFile.toString());
        }
    }

    private static final String FILE_TYPE_NO_MEDIA = ".nomedia";


    @SuppressLint("Range")
    private ArrayList<CustomFile> filterFiles(Context context) {

        ArrayList<CustomFile> listOfHiddenFiles = new ArrayList<CustomFile>();
        String hiddenFilePath;

        // Scan all no Media files
        String nonMediaCondition = MediaStore.Files.FileColumns.MEDIA_TYPE
                + "=" + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE;

        // Files with name contain .nomedia
        String where = nonMediaCondition + " AND "
                + MediaStore.Files.FileColumns.TITLE + " LIKE ?";

        String[] params = new String[] { "%" + FILE_TYPE_NO_MEDIA + "%" };

        // make query for non media files with file title contain ".nomedia" as
        // text on External Media URI
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Files.getContentUri("external"),
                new String[] { MediaStore.Files.FileColumns.DATA }, where,
                params, null);

        // No Hidden file found
        if (cursor.getCount() == 0) {

            listOfHiddenFiles.add(new CustomFile("No Hidden File Found",
                    "Nothing to show Here", "Nothing to show Here", false));

            // show Nothing Found
            return listOfHiddenFiles;
        }

        // Add Hidden file name, path and directory in file object
        while (cursor.moveToNext()) {
            hiddenFilePath = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Files.FileColumns.DATA));
            if (hiddenFilePath != null) {

                listOfHiddenFiles
                        .add(new CustomFile(com.iitp.obsencemediadetector.Images.HiddenImages.FileUtils
                                .getFileName(hiddenFilePath), hiddenFilePath,
                                com.iitp.obsencemediadetector.Images.HiddenImages.FileUtils.getFileParent(hiddenFilePath),
                                isDirHaveImages(FileUtils
                                        .getFileParent(hiddenFilePath))));
            }
        }

        cursor.close();

        return listOfHiddenFiles;

    }

    public boolean isDirHaveImages(String hiddenDirectoryPath) {

        File listFile[] = new File(hiddenDirectoryPath).listFiles();

        boolean dirHaveImages = false;

        if (listFile != null && listFile.length > 0) {
            for (int i = 0; i < listFile.length; i++) {

                if (listFile[i].getName().endsWith(".png")
                        || listFile[i].getName().endsWith(".jpg")
                        || listFile[i].getName().endsWith(".jpeg")
                        || listFile[i].getName().endsWith(".gif")) {

                    // Break even if folder have a single image file
                    dirHaveImages = true;
                    break;

                }
            }
        }
        return dirHaveImages;

    }
}