package com.iitp.obsencemediadetector.Images.ImageFolderScan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iitp.obsencemediadetector.Model.ImageFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.databinding.ActivityImageGalleryFoldersBinding;

import java.util.ArrayList;

public class ImageGalleryFolders extends AppCompatActivity {

    private static final String TAG = "TagGallery";
    ProgressBar progressBar;
    ArrayList<ImageFolder> imageFolders = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_gallery_folders);
        progressBar = findViewById(R.id.gallery_folders_progress_bar);

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
                ImageGalleryAdapter adapter = new ImageGalleryAdapter(getApplicationContext(), imageFolders);
                Log.d(TAG, "onCreate: Adapter Size: " + adapter.getItemCount());
                RecyclerView recyclerView = findViewById(R.id.gallery_folders_rcv);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();

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
            do{
                ImageFolder imageFolder = new ImageFolder();
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));

                Log.d(TAG, "getFoldersList: Folder: " + folderName);
                Log.d(TAG, "getFoldersList: DataPath: " + dataPath);

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
            }while (cursor.moveToNext());
            cursor.close();
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        for (int i = 0; i < foldersList.size(); i++) {
            Log.d(TAG, foldersList.get(i).getFolderName() + " and path = " + foldersList.get(i).getPath() + " " + foldersList.get(i).getNumberOfPics());
        }

        return foldersList;
    }
}