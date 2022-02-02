package com.iitp.obsencemediadetector.Images.ImageFolderScan;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitp.obsencemediadetector.Model.ScannedImages;
import com.iitp.obsencemediadetector.R;

import java.util.ArrayList;

public class ScannedFolderImagesAdapter extends RecyclerView.Adapter<ScannedFolderImagesAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<ScannedImages> scanFolderImagesList;

    public ScannedFolderImagesAdapter(Context mContext, ArrayList<ScannedImages> scanFolderImagesList) {
        this.mContext = mContext;
        this.scanFolderImagesList = scanFolderImagesList;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_scanned_folder_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        ScannedImages scanImages = scanFolderImagesList.get(position);
        holder.fileName.setText(scanImages.getFileName());
        holder.obsceneValue.setText(""+scanImages.getObsceneNature());
        holder.imageView.setImageURI(Uri.parse(scanImages.getImagePathUri()));
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.imageView.getLayoutParams().width = screenWidth/4;
        holder.imageView.getLayoutParams().height = screenWidth/4;
    }

    @Override
    public int getItemCount() {
        return scanFolderImagesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView obsceneValue, fileName;
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.cart_scanned_folder_image_image);
            fileName = itemView.findViewById(R.id.cart_scanned_folder_image_name);
            obsceneValue = itemView.findViewById(R.id.cart_scanned_folder_image_obscene_val);
        }
    }
}
