package com.iitp.obsencemediadetector.Videos.SingleVideos;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitp.obsencemediadetector.R;

import java.util.ArrayList;

public class SingleVideoFramesAdapter extends RecyclerView.Adapter<SingleVideoFramesAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<ScannedVideoImages> scannedVideoImagesArrayList;

    public SingleVideoFramesAdapter(Context mContext, ArrayList<ScannedVideoImages> scannedVideoImagesArrayList) {
        this.mContext = mContext;
        this.scannedVideoImagesArrayList = scannedVideoImagesArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.cart_scanned_folder_images, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannedVideoImages scannedVideoImages = scannedVideoImagesArrayList.get(position);
        holder.imageView.setImageBitmap(scannedVideoImages.getBitmap());
        holder.value.setText("" + scannedVideoImages.getScore());
        holder.secondTime.setText("" + scannedVideoImages.getTime());

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.imageView.getLayoutParams().width = screenWidth/3;
//        holder.imageView.getLayoutParams().height = 2*holder.imageView.getLayoutParams().width;
    }

    @Override
    public int getItemCount() {
        return scannedVideoImagesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView secondTime, value;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cart_scanned_folder_image_image);
            secondTime = itemView.findViewById(R.id.cart_scanned_folder_image_name);
            value = itemView.findViewById(R.id.cart_scanned_folder_image_obscene_val);
        }
    }
}
