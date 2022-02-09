package com.iitp.obsencemediadetector.Videos.FolderVideos;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.BackgroundWork;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.databinding.CartScannedFolderVideosBinding;

import java.util.ArrayList;

public class ScanFolderVideoPartsAdapter extends RecyclerView.Adapter<ScanFolderVideoPartsAdapter.ViewHolder> {

    private static final String TAG = "ScanFolderVideoPartsAdapter";
    private Context mContext;
    private ArrayList<VideoFacer> videoFacerArrayList;
    private ScannedVideo[] scannedVideos;

    public ScanFolderVideoPartsAdapter(Context mContext, ArrayList<VideoFacer> videoFacerArrayList, ScannedVideo[] scannedVideos) {
        this.mContext = mContext;
        this.videoFacerArrayList = videoFacerArrayList;
        this.scannedVideos = new ScannedVideo[videoFacerArrayList.size()];
        for (int i = 0; i < videoFacerArrayList.size(); ++i) {
            this.scannedVideos[i] = null;
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_scanned_folder_videos, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannedVideo scannedVideo = scannedVideos[position];

        Log.d(TAG, "onBindViewHolder: Started: " + position + " @ " + System.currentTimeMillis());

        if (scannedVideo == null) {
            VideoFacer videoFacer = videoFacerArrayList.get(position);
            final ScannedVideo[] result = {null};
            holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.VISIBLE);

            holder.binding.cartScannedFolderVideoName.setText(videoFacer.getVideoName());

            new BackgroundWork((Activity) mContext) {
                @Override
                public void doInBackground() {
                    super.doInBackground();
                    String path = videoFacer.getVideoPath();
                    Uri videoUri = Uri.parse(path);
                    result[0] = MlModel.processVideo(mContext, videoUri);
                }

                @Override
                public void onPostExecute() {
                    super.onPostExecute();
                    holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.GONE);
                    if (result[0] != null) {
                        Log.d(TAG, "onPostExecute: Not Null Result");
                        result[0].setFileName(videoFacer.getVideoName());
                        scannedVideos[holder.getAdapterPosition()] = result[0];
                        ScannedVideo scannedVideo = result[0];
                        holder.binding.cartScannedFolderVideoName.setText(scannedVideo.getFileName());
                        holder.binding.cartScannedFolderVideoValue.setText(scannedVideo.getMaxValue() + "");

                        if (scannedVideo.getBitmapArrayList().size() == 1) {
                            holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                            holder.binding.cartScannedFolderVideoImage2.setVisibility(View.GONE);
                        } else if (scannedVideo.getBitmapArrayList().size() == 2) {
                            holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                            holder.binding.cartScannedFolderVideoImage2.setImageBitmap(scannedVideo.getBitmapArrayList().get(1));
                        }

                        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                        holder.binding.cartScannedFolderVideoImage1.getLayoutParams().width = screenWidth / 3;
                        holder.binding.cartScannedFolderVideoImage2.getLayoutParams().width = screenWidth / 3;

                        Log.d(TAG, "onPostExecute: Pos: " + holder.getAbsoluteAdapterPosition() + "\nVideo: " + scannedVideo.toString());
                    } else {
                        Log.d(TAG, "onPostExecute: Null Result");
                        holder.itemView.setVisibility(View.GONE);
                    }
                    Log.d(TAG, "onBindViewHolder: End Time: " + System.currentTimeMillis());
                }
            }.execute();
        } else {
            holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.GONE);
            holder.binding.cartScannedFolderVideoName.setText(scannedVideo.getFileName());
            holder.binding.cartScannedFolderVideoValue.setText(scannedVideo.getMaxValue() + "");

            if (scannedVideo.getBitmapArrayList().size() == 1) {
                holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                holder.binding.cartScannedFolderVideoImage2.setVisibility(View.GONE);
            } else if (scannedVideo.getBitmapArrayList().size() == 2) {
                holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                holder.binding.cartScannedFolderVideoImage2.setImageBitmap(scannedVideo.getBitmapArrayList().get(1));
            }

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            holder.binding.cartScannedFolderVideoImage1.getLayoutParams().width = screenWidth / 3;
            holder.binding.cartScannedFolderVideoImage2.getLayoutParams().width = screenWidth / 3;
        }
    }

    @Override
    public int getItemCount() {
        return videoFacerArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartScannedFolderVideosBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartScannedFolderVideosBinding.bind(itemView);
        }
    }
}
