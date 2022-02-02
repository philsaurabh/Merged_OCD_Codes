package com.iitp.obsencemediadetector.Videos.FolderVideos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.iitp.obsencemediadetector.Model.VideoFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class VideoGalleryFolders extends AppCompatActivity {

    private static final String TAG = "TagVideoGalleryFolders";
    RecyclerView recyclerView;
    ArrayList<VideoFolder> videoFolderArrayList = new ArrayList<>();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_gallery_folders);
        recyclerView = findViewById(R.id.video_gallery_folder_rcv);
        progressBar = findViewById(R.id.video_gallery_folder_progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        new BackgroundWork(this){
            @Override
            public void doInBackground() {
                super.doInBackground();
                videoFolderArrayList =  getGalleryVideosFolder();
            }

            @Override
            public void onPostExecute() {
                super.onPostExecute();
                VideoGalleryAdapter adapter = new VideoGalleryAdapter(getApplicationContext(), videoFolderArrayList);
                recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }
        }.execute();
    }

    private ArrayList<VideoFolder> getGalleryVideosFolder() {
        ArrayList<String> videoPath = new ArrayList<>();
        ArrayList<VideoFolder> videoFolderArrayList = new ArrayList<>();

        Uri allVideosUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Video.VideoColumns.DATA,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media.BUCKET_ID
        };

        Cursor cursor = getContentResolver().query(allVideosUri, projection, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToNext();
            }

            do {
                String folderName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME));
                String dataPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                String folderPaths = dataPath.substring(0, dataPath.lastIndexOf(folderName + "/"));
                folderPaths += folderName + "/";
                Log.d(TAG, "getFoldersList: FolderPath: " + folderPaths);

                if (!videoPath.contains(folderPaths)) {
                    videoPath.add(folderPaths);

                    VideoFolder videoFolder = new VideoFolder();
                    videoFolder.setFolderName(folderName);
                    Bitmap firstThumb = ThumbnailUtils.createVideoThumbnail(dataPath, MediaStore.Images.Thumbnails.MINI_KIND);
                    videoFolder.setFirstVideo(firstThumb);
                    videoFolder.addVideo();
                    videoFolder.setPath(folderPaths);

                    videoFolderArrayList.add(videoFolder);
                }
                else{
                    for(int i=0; i<videoFolderArrayList.size(); ++i){
                        if(videoFolderArrayList.get(i).getPath().equals(folderPaths)){
                            videoFolderArrayList.get(i).addVideo();
                            Bitmap firstThumb = ThumbnailUtils.createVideoThumbnail(dataPath, MediaStore.Images.Thumbnails.MINI_KIND);
                            videoFolderArrayList.get(i).setFirstVideo(firstThumb);
                        }
                    }
                }

            } while (cursor.moveToNext());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            cursor.close();
        }

        return videoFolderArrayList;
    }
}