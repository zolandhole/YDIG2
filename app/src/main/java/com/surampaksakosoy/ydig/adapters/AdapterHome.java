package com.surampaksakosoy.ydig.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.interfaces.OnLoadMoreListener;
import com.surampaksakosoy.ydig.models.ModelHomeJadi;

import java.util.List;

public class AdapterHome extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 9, VIEW_TYPE_LOADING = 1;
    private List<ModelHomeJadi> dataset;
    private Context context;

    private OnLoadMoreListener onLoadMoreListener;
    private int visibleTreshold= 5;
    private int lastVisibleItem, totalItemCount;
    private boolean isLoading;

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded(){
        isLoading = false;
    }

    public AdapterHome(List<ModelHomeJadi> dataset, Context context, RecyclerView recyclerView){
        this.dataset = dataset;
        this.context = context;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (linearLayoutManager != null) {
                    totalItemCount = linearLayoutManager.getItemCount();
                }
                if (linearLayoutManager != null) {
                    lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                }
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleTreshold)){
                    if (onLoadMoreListener != null){
                        onLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.progressbar_item, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent, false);
            return new ViewItemHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LoadingViewHolder){
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        } else if (holder instanceof ViewItemHolder){
            if (!dataset.get(position).getJudul().equals("")){
                ((ViewItemHolder) holder).textViewJudul.setText(dataset.get(position).getJudul());
                ((ViewItemHolder) holder).textViewJudul.setVisibility(View.VISIBLE);
            } else {
                ((ViewItemHolder) holder).textViewJudul.setVisibility(View.GONE);
            }

            if (!dataset.get(position).getUpload_date().equals("")){
                ((ViewItemHolder) holder).textViewUploadDate.setText(String.valueOf(dataset.get(position).getId()));
                ((ViewItemHolder) holder).textViewUploadDate.setVisibility(View.VISIBLE);
            } else {
                ((ViewItemHolder) holder).textViewUploadDate.setVisibility(View.GONE);
            }

            if (!dataset.get(position).getArab().equals("")){
                ((ViewItemHolder) holder).textViewArab.setText(dataset.get(position).getArab());
                ((ViewItemHolder) holder).textViewArab.setVisibility(View.VISIBLE);
            } else {
                ((ViewItemHolder) holder).textViewArab.setVisibility(View.GONE);
            }

            if (!dataset.get(position).getArti().equals("")){
                ((ViewItemHolder) holder).textViewArti.setText(dataset.get(position).getArti());
                ((ViewItemHolder) holder).textViewArti.setVisibility(View.VISIBLE);
            } else {
                ((ViewItemHolder) holder).textViewArti.setVisibility(View.GONE);
            }

            if (!dataset.get(position).getKontent().equals("")){
                ((ViewItemHolder) holder).textViewKontent.setText(dataset.get(position).getKontent());
                ((ViewItemHolder) holder).textViewKontent.setVisibility(View.VISIBLE);
            } else {
                ((ViewItemHolder) holder).textViewKontent.setVisibility(View.GONE);
            }

            switch (dataset.get(position).getType()) {
                case 0:
                    ((ViewItemHolder) holder).linearLayoutTypeText.setVisibility(View.VISIBLE);
                    ((ViewItemHolder) holder).linearLayoutTypeImage.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeAudio.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeVideo.setVisibility(View.GONE);
                    break;
                case 1:
                    ((ViewItemHolder) holder).linearLayoutTypeText.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeImage.setVisibility(View.VISIBLE);
                    ((ViewItemHolder) holder).linearLayoutTypeAudio.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeVideo.setVisibility(View.GONE);
                    break;
                case 2:
                    ((ViewItemHolder) holder).linearLayoutTypeText.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeImage.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeAudio.setVisibility(View.VISIBLE);
                    ((ViewItemHolder) holder).linearLayoutTypeVideo.setVisibility(View.GONE);
                    break;
                case 4:
                    ((ViewItemHolder) holder).linearLayoutTypeText.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeImage.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeAudio.setVisibility(View.GONE);
                    ((ViewItemHolder) holder).linearLayoutTypeVideo.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
}

    class LoadingViewHolder extends RecyclerView.ViewHolder{
        ProgressBar progressBar;
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBarLoadMore);
        }
    }

    class ViewItemHolder extends RecyclerView.ViewHolder{
        LinearLayout linearLayoutTypeText, linearLayoutTypeImage, linearLayoutTypeAudio, linearLayoutTypeVideo;

        TextView textViewJudul, textViewUploadDate;
        TextView textViewArab, textViewArti;

        private ImageView imageView;

        private FloatingActionButton floatingActionButton;
        private SeekBar seekBar;

        private VideoView videoView;

        TextView textViewKontent;

        ViewItemHolder(@NonNull View itemView) {
            super(itemView);
            linearLayoutTypeText = itemView.findViewById(R.id.typeText);
            linearLayoutTypeImage = itemView.findViewById(R.id.typeImage);
            linearLayoutTypeAudio = itemView.findViewById(R.id.typeAudio);
            linearLayoutTypeVideo = itemView.findViewById(R.id.typeVideo);

            textViewJudul = itemView.findViewById(R.id.txtjudul);
            textViewUploadDate = itemView.findViewById(R.id.txtuploaddate);

            textViewArab = itemView.findViewById(R.id.text_arab);
            textViewArti = itemView.findViewById(R.id.text_arti);

            imageView = itemView.findViewById(R.id.image);

            floatingActionButton = itemView.findViewById(R.id.audio_play);
            seekBar = itemView.findViewById(R.id.audio_seekbar);

            videoView = itemView.findViewById(R.id.video);

            textViewKontent = itemView.findViewById(R.id.kontent);
        }
    }

