package com.surampaksakosoy.ydig.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.models.ModelHome;
import com.surampaksakosoy.ydig.utils.OnLoadMoreListener;

import java.util.List;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class AdapterHome extends RecyclerView.Adapter {

    private final int VIEW_ITEM = 11;
    private final int VIEW_PROG = 10;

    private int visibleTreshold = 5;
    private int lasVisibleItem, totalItemCount;
    private boolean loading;
    private OnLoadMoreListener onLoadMoreListener;

    private List<ModelHome> dataset;
    private Context context;

    public AdapterHome(List<ModelHome> dataset, Context context){
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType){
            case ModelHome.TEXT_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_type_text, parent, false);
                return new TextTypeViewHolder(view);
            case ModelHome.IMAGE_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_type_image, parent, false);
                return new ImageTypeViewHolder(view);
            case ModelHome.AUDIO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_type_audio, parent, false);
                return new AudioTypeViewHolder(view);
            case ModelHome.VIDEO_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_type_video, parent, false);
                return new VideoTypeViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ModelHome object = dataset.get(position);
        if (object != null){
            switch (object.type){
                case ModelHome.TEXT_TYPE:
                    displayText(holder, object);
                    break;

                case ModelHome.IMAGE_TYPE:
                    ((ImageTypeViewHolder) holder).textViewJudul.setText(object.judul);
                    ((ImageTypeViewHolder) holder).textViewKontent.setText(object.konten);
//                    ((ImageTypeViewHolder) holder).imageView.setImageResource(object.data);
                    break;

                case ModelHome.AUDIO_TYPE:
                    ((AudioTypeViewHolder) holder).textViewJudul.setText(object.judul);
                    ((AudioTypeViewHolder) holder).textViewKontent.setText(object.konten);
                    ((AudioTypeViewHolder) holder).floatingActionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(context, "TEST SOUND", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;

                case ModelHome.VIDEO_TYPE:
                    ((VideoTypeViewHolder) holder).textViewJudul.setText(object.judul);
                    ((VideoTypeViewHolder) holder).textViewKontent.setText(object.konten);
                    String videoPath = object.videoPath;
                    Uri uri = Uri.parse(videoPath);
                    ((VideoTypeViewHolder) holder).videoView.setVideoURI(uri);
                    MediaController mediaController = new MediaController(context);
                    ((VideoTypeViewHolder) holder).videoView.setMediaController(mediaController);
                    mediaController.setAnchorView(((VideoTypeViewHolder) holder).videoView);
                    break;
            }
        }
    }

    private void displayText(RecyclerView.ViewHolder holder, ModelHome object) {
//        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/amiribold.ttf");

        if (!object.judul.equals("")){
            ((TextTypeViewHolder) holder).textViewJudul.setText(object.judul);
            ((TextTypeViewHolder) holder).textViewJudul.setVisibility(View.VISIBLE);
        } else {
            ((TextTypeViewHolder) holder).textViewJudul.setVisibility(View.GONE);
        }

        if (!object.arab.equals("")){
//            ((TextTypeViewHolder) holder).textViewArab.setTypeface(tf);
            ((TextTypeViewHolder) holder).textViewArab.setText(object.arab);
            ((TextTypeViewHolder) holder).textViewArab.setVisibility(View.VISIBLE);
        } else {
            ((TextTypeViewHolder) holder).textViewArab.setVisibility(View.GONE);
        }

        if (!object.judul.equals("")){
            ((TextTypeViewHolder) holder).textViewArti.setText(object.arti);
            ((TextTypeViewHolder) holder).textViewArti.setVisibility(View.VISIBLE);
        } else {
            ((TextTypeViewHolder) holder).textViewArti.setVisibility(View.GONE);
        }

        if (!object.judul.equals("")){
            ((TextTypeViewHolder) holder).textViewKontent.setText(object.konten);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ((TextTypeViewHolder) holder).textViewKontent.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
            }
            ((TextTypeViewHolder) holder).textViewKontent.setVisibility(View.VISIBLE);
        } else {
            ((TextTypeViewHolder) holder).textViewKontent.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        switch (dataset.get(position).type){
            case 0: return ModelHome.TEXT_TYPE;
            case 1: return ModelHome.IMAGE_TYPE;
            case 2: return ModelHome.AUDIO_TYPE;
            case 3: return ModelHome.VIDEO_TYPE;
            default: return -1;
        }
    }

    public static class TextTypeViewHolder extends RecyclerView.ViewHolder{
        TextView textViewJudul, textViewArab, textViewArti, textViewKontent;
        TextTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJudul = itemView.findViewById(R.id.text_judul);
            textViewArab = itemView.findViewById(R.id.text_arab);
            textViewArti = itemView.findViewById(R.id.text_arti);
            textViewKontent = itemView.findViewById(R.id.text_kontent);
        }
    }

    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder{
        TextView textViewJudul, textViewKontent;
        ImageView imageView;
        ImageTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJudul = itemView.findViewById(R.id.image_judul);
            textViewKontent = itemView.findViewById(R.id.image_kontent);
            imageView = itemView.findViewById(R.id.image_image);
        }
    }

    public static class AudioTypeViewHolder extends RecyclerView.ViewHolder{
        TextView textViewJudul, textViewKontent;
        FloatingActionButton floatingActionButton;
        SeekBar seekBar;
        AudioTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJudul = itemView.findViewById(R.id.audio_judul);
            textViewKontent = itemView.findViewById(R.id.audio_kontent);
            floatingActionButton = itemView.findViewById(R.id.audio_fab);
            seekBar = itemView.findViewById(R.id.audio_seekbar);
        }
    }

    public static class VideoTypeViewHolder extends RecyclerView.ViewHolder{
        TextView textViewJudul, textViewKontent;
        VideoView videoView;
        VideoTypeViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewJudul = itemView.findViewById(R.id.video_judul);
            textViewKontent = itemView.findViewById(R.id.video_kontent);
            videoView = itemView.findViewById(R.id.video_video);
        }
    }
}
