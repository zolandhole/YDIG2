package com.surampaksakosoy.ydig.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.models.ModelPanduan;

import java.util.List;

public class AdapterPanduan extends RecyclerView.Adapter<AdapterPanduan.Holder> {

    private List<ModelPanduan> modelPanduan;
    private Context context;
//    private static final String TAG = "AdapterPanduan";

    public AdapterPanduan(Context context, List<ModelPanduan> modelPanduan){
        this.context = context;
        this.modelPanduan = modelPanduan;
    }

    @NonNull
    @Override
    public AdapterPanduan.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_panduan, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPanduan.Holder holder, int position) {
        ModelPanduan panduan = modelPanduan.get(position);
        if (!panduan.getImage_path().equals("")){
            Glide.with(context)
                    .load(panduan.getImage_path())
                    .centerCrop()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            holder.image_failed.setVisibility(View.VISIBLE);
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            holder.image_failed.setVisibility(View.GONE);
                            holder.progressBar.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(holder.imageView);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.judul.setText(panduan.getJudul());
        holder.deskripsi.setText(panduan.getDeskripsi());
    }

    @Override
    public int getItemCount() {
        return modelPanduan.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView judul, deskripsi, image_failed;
        ProgressBar progressBar;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.panduan_image);
            judul = itemView.findViewById(R.id.panduan_judul);
            deskripsi = itemView.findViewById(R.id.panduan_deskripsi);
            progressBar = itemView.findViewById(R.id.panduan_loading_image);
            image_failed = itemView.findViewById(R.id.panduan_loading_image_failed);
        }
    }
}
