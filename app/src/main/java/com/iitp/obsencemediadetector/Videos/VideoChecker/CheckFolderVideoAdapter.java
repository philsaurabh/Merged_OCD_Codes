package com.iitp.obsencemediadetector.Videos.VideoChecker;

import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iitp.obsencemediadetector.Model.VideoFacer;
import com.iitp.obsencemediadetector.R;
import com.iitp.obsencemediadetector.Utils.MlModel;
import com.iitp.obsencemediadetector.databinding.CartCheckVideoBinding;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckFolderVideoAdapter extends RecyclerView.Adapter<CheckFolderVideoAdapter.ViewHolder>{

    private static final String TAG = "CheckFolderVideoAdapter";
    private Context mContext;
    private ArrayList<VideoFacer> VideoFacerArrayList;
    private int[] scoreList;
    protected ScanVideo scanVideo;


    public CheckFolderVideoAdapter(Context mContext, ArrayList<VideoFacer> VideoFacerArrayList, ScanVideo scanVideo) {
        this.mContext = mContext;
        this.VideoFacerArrayList = VideoFacerArrayList;
        this.scoreList = new int[VideoFacerArrayList.size()];
        for(int i=0; i<VideoFacerArrayList.size(); ++i){
            this.scoreList[i] = -1;
        }

        this.scanVideo = scanVideo;
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

        Glide.with(mContext).load(videoFacer.getVideoPath()).into(holder.binding.cartCheckVideoImage);

        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        holder.binding.cartCheckVideoImage.getLayoutParams().width = screenWidth/3;
        holder.binding.cartCheckVideoImage.getLayoutParams().height = screenWidth/3;
        holder.binding.cartCheckVideoFileName.setText(videoFacer.getVideoName());

        int redColor = mContext.getResources().getColor(R.color.red);
        int blackColor = mContext.getResources().getColor(R.color.black);

        if(scoreList[position]!=-1){
            holder.binding.cartCheckVideoProgressBar.setVisibility(View.GONE);
//            scanVideo.scanDone();
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
            int pos = holder.getBindingAdapterPosition();

///*
            Handler handler = new Handler(Looper.getMainLooper());

            ExecutorService executorService = Executors.newSingleThreadExecutor();

            executorService.submit(() -> {
                Log.d(TAG, "onBindViewHolder: Submitted executor " + pos);
                String videoUri = videoFacer.getVideoPath();
                MlModel mlModel = new MlModel();
                isObscene[0] = mlModel.isVideoObscene(mContext, Uri.parse(videoUri));

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        holder.binding.cartCheckVideoProgressBar.setVisibility(View.GONE);

                        scanVideo.scanDone(videoUri);
                        if (isObscene[0]) {
                            scoreList[pos] = 1;
                            holder.binding.cartCheckVideoStatus.setText("Obscene");
                            holder.binding.cartCheckVideoStatus.setTextColor(redColor);
                        } else {
                            scoreList[pos] = 0;
                            holder.binding.cartCheckVideoStatus.setText("Non - Obscene");
                            holder.binding.cartCheckVideoStatus.setTextColor(blackColor);
                        }
                    }
                });
            });
            executorService.shutdown();
//*/

/*
            new BackgroundWork((Activity) mContext){
                @Override
                public void doInBackground() {
                    super.doInBackground();
                    String videoUri = videoFacer.getVideoPath();
                    MlModel mlModel = new MlModel();
                    isObscene[0] = mlModel.isVideoObscene(mContext, Uri.parse(videoUri));
                }

                @Override
                public void onPostExecute() {
                    super.onPostExecute();
                    holder.binding.cartCheckVideoProgressBar.setVisibility(View.GONE);

                    scanVideo.scanDone(videoFacer.getVideoPath());
                    if (isObscene[0]) {
                        scoreList[pos] = 1;
                        holder.binding.cartCheckVideoStatus.setText("Obscene");
                        holder.binding.cartCheckVideoStatus.setTextColor(redColor);
                    } else {
                        scoreList[pos] = 0;
                        holder.binding.cartCheckVideoStatus.setText("Non - Obscene");
                        holder.binding.cartCheckVideoStatus.setTextColor(blackColor);
                    }
                }
            }.execute();
*/
        }
    }

    @Override
    public int getItemCount() {
        return VideoFacerArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartCheckVideoBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartCheckVideoBinding.bind(itemView);
        }
    }

    public interface ScanVideo{
        void scanDone(String videoUri);
    }

}
