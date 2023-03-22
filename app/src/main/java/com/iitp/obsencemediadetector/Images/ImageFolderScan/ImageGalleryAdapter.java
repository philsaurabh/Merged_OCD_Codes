package com.iitp.obsencemediadetector.Images.ImageFolderScan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.iitp.obsencemediadetector.Model.ImageFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.databinding.CartGalleryFoldersBinding;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ImageGalleryAdapter extends RecyclerView.Adapter<ImageGalleryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<ImageFolder> imageFolderArrayList;

    private static final String TAG = "TagGalleryAdapter";

    public ImageGalleryAdapter(Context mContext, ArrayList<ImageFolder> imageFolderArrayList) {
//        Log.d(TAG, "GalleryAdapter: Const Called");
        this.mContext = mContext;
        this.imageFolderArrayList = imageFolderArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Log.d(TAG, "onCreateViewHolder: ");
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_gallery_folders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ImageFolder imageFolder = imageFolderArrayList.get(position);
//        Picasso.get().load(new File(imageFolder.getFirstPic())).into(holder.binding.cartGalleryFolderImage);
//        holder.binding.cartGalleryFolderImage.setImageURI(Uri.parse(imageFolder.getFirstPic()));
        Glide.with(mContext)
                .load(imageFolder.getFirstPic())
                .apply(new RequestOptions().centerCrop())
                .into(holder.binding.cartGalleryFolderImage);
        holder.binding.cartGalleryFolderName.setText(imageFolder.getNumberOfPics() + " " + imageFolder.getFolderName());
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.binding.cartGalleryFolderImage.getLayoutParams().width = width / 3;
        holder.binding.cartGalleryFolderImage.getLayoutParams().height = width / 3;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String folderGson = gson.toJson(imageFolder);
                Intent intent = new Intent(mContext, ScanFolderImages.class);
                intent.putExtra("folderGson", folderGson);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: Size: " + imageFolderArrayList.size());
        return imageFolderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartGalleryFoldersBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            Log.d(TAG, "ViewHolder: ");
            binding = CartGalleryFoldersBinding.bind(itemView);
        }
    }
}
