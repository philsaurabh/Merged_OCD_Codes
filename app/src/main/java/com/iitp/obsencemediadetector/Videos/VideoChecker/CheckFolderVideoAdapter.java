package com.iitp.obsencemediadetector.Videos.VideoChecker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.databinding.CartCheckVideoBinding;

import java.util.ArrayList;

public class CheckFolderVideoAdapter extends RecyclerView.Adapter<CheckFolderVideoAdapter.ViewHolder>{

    private static final String TAG = "CheckFolderVideoAdapter";
    private Context mContext;
    private ArrayList<VideoFacer> VideoFacerArrayList;
    private int[] scoreList;

    public CheckFolderVideoAdapter(Context mContext, ArrayList<VideoFacer> VideoFacerArrayList) {
        this.mContext = mContext;
        this.VideoFacerArrayList = VideoFacerArrayList;
        this.scoreList = new int[VideoFacerArrayList.size()];
        for(int i=0; i<VideoFacerArrayList.size(); ++i){
            this.scoreList[i] = -1;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_check_video, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoFacer videoFacer = VideoFacerArrayList.get(position);
        Bitmap firstThumb = ThumbnailUtils.createVideoThumbnail(videoFacer.getVideoPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        if(firstThumb!=null){
            holder.binding.cartCheckVideoImage.setImageBitmap(firstThumb);
        }

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.binding.cartCheckVideoImage.getLayoutParams().width = screenWidth/3;
        holder.binding.cartCheckVideoImage.getLayoutParams().height = screenWidth/3;

        holder.binding.cartCheckVideoFileName.setText(videoFacer.getVideoName());

        int redColor = mContext.getResources().getColor(R.color.red);
        int blackColor = mContext.getResources().getColor(R.color.black);
        if(scoreList[position]!=-1){
            holder.binding.cartCheckVideoProgressBar.setVisibility(View.GONE);
            if(scoreList[position]==0){
                holder.binding.cartCheckVideoStatus.setText("Obscene");
                holder.binding.cartCheckVideoStatus.setTextColor(redColor);
            }
            else if(scoreList[position]==1){
                holder.binding.cartCheckVideoStatus.setText("Non - Obscene");
                holder.binding.cartCheckVideoStatus.setTextColor(blackColor);
            }
        }else {
            holder.binding.cartCheckVideoProgressBar.setVisibility(View.VISIBLE);
            final boolean[] isObscene = {false};
            new BackgroundWork((Activity) mContext) {
                @Override
                public void doInBackground() {
                    super.doInBackground();
                    String videoUri = videoFacer.getVideoPath();
                    isObscene[0] = MlModel.isVideoObscene(mContext, Uri.parse(videoUri));
                }

                @Override
                public void onPostExecute() {
                    super.onPostExecute();
                    holder.binding.cartCheckVideoProgressBar.setVisibility(View.GONE);
                    Log.d(TAG, "onPostExecute: End time: " + System.currentTimeMillis());
                    if (isObscene[0]) {
                        scoreList[holder.getAdapterPosition()] = 1;
                        holder.binding.cartCheckVideoStatus.setText("Obscene");
                        holder.binding.cartCheckVideoStatus.setTextColor(redColor);
                    } else {
                        scoreList[holder.getAdapterPosition()] = 0;
                        holder.binding.cartCheckVideoStatus.setText("Non - Obscene");
                        holder.binding.cartCheckVideoStatus.setTextColor(blackColor);
                    }
                }
            }.execute();
        }
    }

    @Override
    public int getItemCount() {
        return VideoFacerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartCheckVideoBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartCheckVideoBinding.bind(itemView);
        }
    }
}
