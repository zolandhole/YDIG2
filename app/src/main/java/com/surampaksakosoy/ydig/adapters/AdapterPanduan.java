package com.surampaksakosoy.ydig.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.models.ModelPanduan;

import java.util.List;

public class AdapterPanduan extends RecyclerView.Adapter<AdapterPanduan.Holder> {

    private List<ModelPanduan> modelPanduan;
    private Context context;
//    private static final String TAG = "AdapterPanduan";

    public AdapterPanduan(List<ModelPanduan> modelPanduans, Context context){
        this.modelPanduan = modelPanduans;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPanduan.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_panduan, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPanduan.Holder holder, int position) {
        final ModelPanduan panduan = modelPanduan.get(position);
        holder.judul.setText(panduan.getJudul());
        byte[] imagePanduan = panduan.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(imagePanduan, 0, imagePanduan.length);
        holder.imageView.setImageBitmap(bitmap);
        holder.panduan_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, panduan.getPanduan_id() + panduan.getId(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public int getItemCount() {
        return modelPanduan.size();
    }


    class Holder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView judul;
        LinearLayout panduan_parent;

        Holder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.panduan_image);
            judul = itemView.findViewById(R.id.panduan_judul);
            panduan_parent = itemView.findViewById(R.id.panduan_parent);
        }
    }
}
