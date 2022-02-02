package com.iitp.obsencemediadetector.Videos.FolderVideos;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.databinding.CartScannedFolderVideosBinding;

import java.util.ArrayList;

public class ScannedVideoAdapter extends RecyclerView.Adapter<ScannedVideoAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<ScannedVideo> scannedVideoArrayList;

    public ScannedVideoAdapter(Context mContext, ArrayList<ScannedVideo> scannedVideoArrayList) {
        this.mContext = mContext;
        this.scannedVideoArrayList = scannedVideoArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_scanned_folder_videos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannedVideo scannedVideo = scannedVideoArrayList.get(position);
        holder.binding.cartScannedFolderVideoName.setText(scannedVideo.getFileName());
        holder.binding.cartScannedFolderVideoValue.setText(scannedVideo.getMaxValue() + "");

        if(scannedVideo.getBitmapArrayList().size()==1){
            holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
            holder.binding.cartScannedFolderVideoImage2.setVisibility(View.GONE);
        }
        else if(scannedVideo.getBitmapArrayList().size()==2){
            holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
            holder.binding.cartScannedFolderVideoImage2.setImageBitmap(scannedVideo.getBitmapArrayList().get(1));
        }

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.binding.cartScannedFolderVideoImage1.getLayoutParams().width = screenWidth/3;
        holder.binding.cartScannedFolderVideoImage2.getLayoutParams().width = screenWidth/3;
    }

    @Override
    public int getItemCount() {
        return scannedVideoArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartScannedFolderVideosBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartScannedFolderVideosBinding.bind(itemView);
        }
    }
}
