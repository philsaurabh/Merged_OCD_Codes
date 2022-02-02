package com.iitp.obsencemediadetector.Videos.FolderVideos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.iitp.obsencemediadetector.Images.ImageFolderScan.ScanFolderImages;
import com.iitp.obsencemediadetector.Model.VideoFolder;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.databinding.CartGalleryFoldersBinding;

import java.util.ArrayList;

public class VideoGalleryAdapter extends RecyclerView.Adapter<VideoGalleryAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<VideoFolder> videoFolderArrayList;

    public VideoGalleryAdapter(Context mContext, ArrayList<VideoFolder> videoFolderArrayList) {
        this.mContext = mContext;
        this.videoFolderArrayList = videoFolderArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_gallery_folders, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoFolder videoFolder = videoFolderArrayList.get(position);

        holder.binding.cartGalleryFolderImage.setImageBitmap(videoFolder.getFirstVideo());
        holder.binding.cartGalleryFolderName.setText(videoFolder.getNumOfVideos() + " " + videoFolder.getFolderName());
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.binding.cartGalleryFolderImage.getLayoutParams().width = width / 3;
        holder.binding.cartGalleryFolderImage.getLayoutParams().height = width / 3;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ScanFolderVideos.class);
                String videoGson = new Gson().toJson(videoFolder);
                intent.putExtra("VideoGson", videoGson);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return videoFolderArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartGalleryFoldersBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartGalleryFoldersBinding.bind(itemView);
        }
    }
}
