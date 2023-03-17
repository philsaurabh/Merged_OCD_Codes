package com.iitp.obsencemediadetector.Images.ImageFolderScan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iitp.obsencemediadetector.MainActivity;
import com.iitp.obsencemediadetector.Model.ImageFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.databinding.ActivityImageGalleryFoldersBinding;

import java.util.ArrayList;

public class ImageGalleryFolders extends AppCompatActivity {

    private static final String TAG = "TagGallery";
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<ImageFolder> imageFolders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_folders);

        Log.d(TAG, "onCreate: Start: " + System.currentTimeMillis());

        _init();

        progressBar.setVisibility(View.VISIBLE);
        new BackgroundWork(this){
            @Override
            public void doInBackground() {
                super.doInBackground();
                imageFolders = getFoldersList();
                Log.d(TAG, "onCreate: Size: " + imageFolders.size());
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                Log.d(TAG, "onCreate: Photos Fetched: " + System.currentTimeMillis());
                ImageGalleryAdapter adapter = new ImageGalleryAdapter(ImageGalleryFolders.this, imageFolders);
                Log.d(TAG, "onCreate: Adapter Size: " + adapter.getItemCount());
                recyclerView.setLayoutManager(new GridLayoutManager(ImageGalleryFolders.this, 3));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();

    }

    private void _init() {
        progressBar = findViewById(R.id.gallery_folders_progress_bar);
        recyclerView = findViewById(R.id.gallery_folders_rcv);
//        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
//        recyclerView.getLayoutParams().height = height;
//        recyclerView.setHasFixedSize(false);
//        recyclerView.setNestedScrollingEnabled(false);
    }

    private ArrayList<ImageFolder> getFoldersList(){
        ArrayList<ImageFolder> foldersList = new ArrayList<>();
        ArrayList<String> picPath = new ArrayList<>();

        Uri allImagesUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID
        };

        Cursor cursor = getContentResolver().query(allImagesUri,projection, null, null, null);

        try{
            if(cursor!=null){
                cursor.moveToNext();
            }
            else{
                Log.d(TAG, "getFoldersList: Null cursor");
            }
            do{
                ImageFolder imageFolder = new ImageFolder();
                String folderName;
                String dataPath;
                if (cursor != null) {

                    folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                    Log.d(TAG, "getFoldersList: Folder: " + folderName);
                    Log.d(TAG, "getFoldersList: DataPath: " + dataPath);

                    if(dataPath.lastIndexOf(folderName + "/")<=0){
                        Log.d(TAG, "getFoldersList: NegativePath: " + dataPath);
                        continue;
                    }

                    String folderPaths = dataPath.substring(0, dataPath.lastIndexOf(folderName + "/"));
                    folderPaths +=  folderName + "/";
                    Log.d(TAG, "getFoldersList: FolderPath: " + folderPaths);

                    if(!picPath.contains(folderPaths)){
                        picPath.add(folderPaths);

                        imageFolder.setPath(folderPaths);
                        imageFolder.setFolderName(folderName);
                        imageFolder.setFirstPic(dataPath); //if the folder has only one picture this line helps to set it as first so as to avoid blank image in itemview
                        imageFolder.addPics();
                        foldersList.add(imageFolder);
                    }
                    else{
                        for(int i=0; i<foldersList.size(); ++i){
                            if(foldersList.get(i).getPath().equals(folderPaths)){
                                foldersList.get(i).setFirstPic(dataPath);
                                foldersList.get(i).addPics();
                            }
                        }
                    }

                }
                else{
                    Log.d(TAG, "getFoldersList: Cursor is Null");
                }
            }while (cursor!=null && cursor.moveToNext());
            if(cursor!=null) {
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
//            Toast.makeText(ImageGalleryFolders.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < foldersList.size(); i++) {
            Log.d(TAG, foldersList.get(i).getFolderName() + " and path = " + foldersList.get(i).getPath() + " " + foldersList.get(i).getNumberOfPics());
        }

        return foldersList;
    }
}