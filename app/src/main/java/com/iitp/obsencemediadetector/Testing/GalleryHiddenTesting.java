package com.iitp.obsencemediadetector.Testing;

import static java.lang.Thread.sleep;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.iitp.obsencemediadetector.Model.ImageFolder;
import com.iitp.obsencemediadetector.R;

import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GalleryHiddenTesting extends AppCompatActivity {

    private static final String TAG = "GalleryHiddenTestingTag";
    ArrayList<File> mImageFileList = new ArrayList<>();
    private ArrayList<Folder> mAllMediaFolderList = new ArrayList<Folder>();
    private ArrayList<Folder> mUniqFolderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_hidden_testing);

        File ROOT_DIR = Environment.getExternalStorageDirectory();
        sortDirectory(ROOT_DIR);

        mAllMediaFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder");
        Log.d(TAG, "onCreate: AllMediaSize: " + mAllMediaFolderList.size());
        if (mAllMediaFolderList.size() != 0) {
            mUniqFolderList = new ArrayList<>();
            mUniqFolderList = getAllUniqueFolders(mAllMediaFolderList);
            Log.d(TAG, "UniqueSize: " + String.valueOf(mUniqFolderList.size()));

            for (Folder folder : mUniqFolderList) {
                Log.d(TAG, "Folder: " + folder.getFolderName() + " " + folder.getFileCount());
            }
        }
    }


    private ArrayList<Folder> getAllUniqueFolders(ArrayList<Folder> listOfAllImages) {

        ArrayList<String> hiddenFolders = new ArrayList<>();
        ArrayList<String> hiddenImagesPath = new ArrayList<>();
        ArrayList<Folder> uniqueHiddenImageFolder = new ArrayList<>();
        HashMap<String, Integer> fileCount = new HashMap<>();

        for (Folder folder : listOfAllImages) {
            if (isImageFile(folder.getImagePath())) {
                String[] locn = folder.getImagePath().split("/");
                String imageName = locn[locn.length - 1];
                if (folder.getFolderName().startsWith(".") || imageName.startsWith(".")) {
                    if (!hiddenFolders.contains(folder.getFolderPath())) {
                        if (!hiddenImagesPath.contains(folder.getImagePath())) {
                            fileCount.put(folder.getFolderPath(), 1);
                            hiddenImagesPath.add(folder.getImagePath());
                            Log.d(TAG, "fileCount: " +  folder.getImagePath());
                        }
                        folder.setFolderName(folder.getFolderName());
                        folder.setImagePath(folder.getImagePath());
                        folder.setSelected(false);
                        hiddenFolders.add(folder.getFolderPath());
                        uniqueHiddenImageFolder.add(folder);
                    } else {
                        if (!hiddenImagesPath.contains(folder.getImagePath())) {
                            fileCount.put(folder.getFolderPath(), fileCount.get(folder.getFolderPath()) + 1);
                            Log.d(TAG, "fileCount: " +  folder.getImagePath());
                            hiddenImagesPath.add(folder.getImagePath());
                        }
                    }
                }
            }
        }

        for (Folder folder : uniqueHiddenImageFolder) {
            int ct = fileCount.get(folder.getFolderPath());
            folder.setFileCount(ct);
        }

        return uniqueHiddenImageFolder;


        /*
        ArrayList<Folder> listOfUniqueImages = new ArrayList<Folder>();
        List<String> enemyIds = new ArrayList<String>();
        for (Folder folder : listOfAllImages) {
            if (!enemyIds.contains(folder.getFolderPath())) {
                enemyIds.add(folder.getFolderPath());
                folder.setFolderName(folder.getFolderName() + " (" + getMediaCount(new File(folder.getFolderPath())) + ")");
                folder.setImagePath(folder.getImagePath());
                folder.setSelected(false);
                Log.d(TAG, "FileType: " +  folder.getImagePath());
                if (isImageFile(folder.getImagePath())) {
                    if (folder.getFolderName().startsWith(".")) {
                        listOfUniqueImages.add(folder);
                        Log.d(TAG, "getAllUniqueFolders: Hidden Images: " + folder.getImagePath());
                    } else if (!folder.getImagePath().contains(".nomedia")) {
                        listOfUniqueImages.add(folder);
                        Log.d(TAG, "getAllUniqueFolders: Hidden Images: " + folder.getImagePath());
                    }
                    String[] locn = folder.getImagePath().split("/");
                    String imageName = locn[locn.length-1];
                    Log.d(TAG, "getAllUniqueFolders: ImagePath: " + folder.getImagePath());
                    if(folder.getFolderName().startsWith(".") || imageName.startsWith(".")){
                        Log.d(TAG, "getAllUniqueFolders: Hidden Image: " + folder.getImagePath());
                    }
                }
            }
        }
        return listOfUniqueImages;
         */


    }
    void sortDirectory(File mfile) {
        ArrayList<File> mDirectoryList = new ArrayList<>();
        String name = null;
        File[] mFileLists = mfile.listFiles();
        try {
            for (int i = 0; i < mFileLists.length; i++) {
                if (mFileLists[i].isDirectory()) {

                    Log.d("file length ", String.valueOf(mFileLists.length));
                    mDirectoryList.add(mFileLists[i]);
                    if (mFileLists[i].getParent().contains("/storage/emulated/0/Download/New folder2/")) {
                        Log.d("new folder getname ", new File(mFileLists[i].getParent()).getName());
                        Log.d("new folder parent ", (mFileLists[i].getAbsolutePath()));
                        Log.d("new folder absolute ", mFileLists[i].getAbsolutePath());
                        Folder newfolder = new Folder();
                        newfolder.setFolderPath(mFileLists[i].getAbsolutePath());
                        newfolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        newfolder.setImagePath(mFileLists[i].getAbsolutePath());

                        //    mAllMediaFolderList.add(newfolder);
                        File newfile = new File(mFileLists[i].getAbsolutePath());
                        if (newfile.isDirectory()) {

                            File[] mNewFolderContents = newfile.listFiles();
                            Log.d("mNewFolderContents ", String.valueOf(mNewFolderContents.length));
                            for (int j = 0; j < mNewFolderContents.length; j++) {
                                Log.d("absolute ", mNewFolderContents[j].getAbsolutePath());
                                newfolder.setImagePath(mNewFolderContents[j].getAbsolutePath());
                                Log.d("new folder absolute ", mFileLists[i].getAbsolutePath());
                                Log.d("new folder parent ", (mFileLists[i].getParent()));

                                ArrayList<Folder> mFolderList1 = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mNewFolderContents[j].getAbsolutePath()));

                                if (mFolderList1.size() == 0) {
                                    newfolder.save();
                                }

                            }
                        }


                    }
                } else {
                    if (isImageFile(mFileLists[i].getAbsolutePath())) {
                        mImageFileList.add(mFileLists[i]);
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                        Log.v(" filename is ", mFileLists[i].getPath());

                        /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/

                        ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));


                        if (mFolderList.size() == 0) {
                            mFolder.save();
                        } else {
                            Log.v("mFolderList", String.valueOf(mFolderList.size()));
                        }
                        //    mAllMediaFolderList.add(mFolder);
                    } else if (isVideoFile(mFileLists[i].getAbsolutePath())) {
                        mImageFileList.add(mFileLists[i]);
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());
                        /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/
                        ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));

                        if (mFolderList.size() == 0) {
                            mFolder.save();
                        }

                        //   mAllMediaFolderList.add(mFolder);
                    } else if (isNoMediaFile(mFileLists[i].getAbsolutePath())) {
                        Folder mFolder = new Folder();
                        mFolder.setFolderPath(mFileLists[i].getParent());
                        mFolder.setFolderName(new File(mFileLists[i].getParent()).getName());
                        mFolder.setImagePath(mFileLists[i].getAbsolutePath());

                        try {
                            /*ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath = " + mFileLists[i].getParent() + " and imagePath = " + mFileLists[i].getAbsolutePath());*/

                            ArrayList<Folder> mFolderList = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folder_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getParent()) + " and image_path = " + DatabaseUtils.sqlEscapeString(mFileLists[i].getAbsolutePath()));


                            /*  ArrayList<Folder> mFolderList1 = (ArrayList<Folder>) Folder.findWithQuery(Folder.class, "Select * from Folder where folderPath ="+"/storage/emulated/0/Download"+ " and imagePath = "+"/storage/emulated/0/Download/.nomedia");
                             */


                            if (mFolderList.size() == 0) {
                                mFolder.save();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        //    mAllMediaFolderList.add(mFolder);
                        Log.v(" nomedia files are: ", mFileLists[i].getAbsolutePath());
                    } else {
                        Log.v(" isnomedia filename? ", mFileLists[i].getAbsolutePath());
                    }
                }
            }
            for (int j = 0; j < mDirectoryList.size(); j++) {
                Log.v(" else filename is ", mDirectoryList.get(j).getAbsolutePath());
                sortDirectory(mDirectoryList.get(j));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isImageFile(String path) {
//       /*
       String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
//        */

        /*
        if ((path.endsWith(".jpeg")) || (path.endsWith(".png")) || (path.endsWith(".gif"))) {
            return true;
        } else return false;
         */
    }

    public static boolean isNoMediaFile(String path) {
        //  String mimeType = URLConnection.guessContentTypeFromName(path);
        if (path.endsWith(".nomedia")) {
            return true;
        } else return false;
    }


    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }

}