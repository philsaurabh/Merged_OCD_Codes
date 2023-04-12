package com.iitp.obsencemediadetector.Videos.FolderVideos;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iitp.obsencemediadetector.Model.ScannedVideo;
import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.databinding.CartScannedFolderVideosBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScanFolderVideoPartsAdapter extends RecyclerView.Adapter<ScanFolderVideoPartsAdapter.ViewHolder> {

    private static final String TAG = "ScanFolderVideoPartsAdapter";
    private Context mContext;
    private ArrayList<VideoFacer> videoFacerArrayList;
    private ScannedVideo[] scannedVideos;
    protected ScanVideo scanVideo;

    public ScanFolderVideoPartsAdapter(Context mContext, ArrayList<VideoFacer> videoFacerArrayList, int size, ScanVideo scanVideo) {
        this.mContext = mContext;
        this.videoFacerArrayList = videoFacerArrayList;
        this.scannedVideos = new ScannedVideo[size];
        this.scanVideo = scanVideo;
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

        if (scannedVideo == null) {
            VideoFacer videoFacer = videoFacerArrayList.get(position);
            final ScannedVideo[] result = {null};

            holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.VISIBLE);
            holder.binding.cartScannedFolderVideoName.setText(videoFacer.getVideoName());

            Handler handler = new Handler(mContext.getMainLooper());

            int pos = holder.getBindingAdapterPosition();

            // Executor Code
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    String path = videoFacer.getVideoPath();
                    Uri videoUri = Uri.parse(path);
                    MlModel mlModel = new MlModel();
                    result[0] = mlModel.processVideo(mContext, videoUri);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.GONE);
                            scanVideo.scanDone(path);
                            if (result[0] != null) {
                                result[0].setFileName(videoFacer.getVideoName());
                                scannedVideos[pos] = result[0];
                                ScannedVideo scannedVideo = result[0];
                                holder.binding.cartScannedFolderVideoName.setText(scannedVideo.getFileName());
                                holder.binding.cartScannedFolderVideoValue.setText(scannedVideo.getMaxValue() + "");

                                if (scannedVideo.getBitmapArrayList().size() == 1) {
//                                    holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                                    Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(0)).into(holder.binding.cartScannedFolderVideoImage1);
                                    holder.binding.cartScannedFolderVideoImage2.setVisibility(View.GONE);
                                } else if (scannedVideo.getBitmapArrayList().size() == 2) {
                                    Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(0)).into(holder.binding.cartScannedFolderVideoImage1);
                                    Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(1)).into(holder.binding.cartScannedFolderVideoImage2);
//                                    holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
//                                    holder.binding.cartScannedFolderVideoImage2.setImageBitmap(scannedVideo.getBitmapArrayList().get(1));
                                }

                                int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
                                holder.binding.cartScannedFolderVideoImage1.getLayoutParams().width = screenWidth / 3;
                                holder.binding.cartScannedFolderVideoImage2.getLayoutParams().width = screenWidth / 3;
                            } else {
                                holder.itemView.setVisibility(View.GONE);
                                holder.itemView.getLayoutParams().height = 0;
                            }
                        }
                    });

                }
            });
            executorService.shutdown();

        } else {
            holder.binding.cartScannedFolderVideoProgressBar.setVisibility(View.GONE);
            holder.binding.cartScannedFolderVideoName.setText(scannedVideo.getFileName());
            holder.binding.cartScannedFolderVideoValue.setText(scannedVideo.getMaxValue() + "");

            if (scannedVideo.getBitmapArrayList().size() == 1) {
//                                    holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
                Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(0)).into(holder.binding.cartScannedFolderVideoImage1);
                holder.binding.cartScannedFolderVideoImage2.setVisibility(View.GONE);
            } else if (scannedVideo.getBitmapArrayList().size() == 2) {
                Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(0)).into(holder.binding.cartScannedFolderVideoImage1);
                Glide.with(mContext).load(scannedVideo.getBitmapArrayList().get(1)).into(holder.binding.cartScannedFolderVideoImage2);
//                                    holder.binding.cartScannedFolderVideoImage1.setImageBitmap(scannedVideo.getBitmapArrayList().get(0));
//                                    holder.binding.cartScannedFolderVideoImage2.setImageBitmap(scannedVideo.getBitmapArrayList().get(1));
            }

            int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            holder.binding.cartScannedFolderVideoImage1.getLayoutParams().width = screenWidth / 3;
            holder.binding.cartScannedFolderVideoImage2.getLayoutParams().width = screenWidth / 3;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
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

    public interface ScanVideo{
        void scanDone(String videoUri);
    }
}
