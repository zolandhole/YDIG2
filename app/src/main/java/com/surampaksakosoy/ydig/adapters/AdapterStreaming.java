package com.surampaksakosoy.ydig.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.models.ModelStreaming;

import java.util.List;

public class AdapterStreaming extends RecyclerView.Adapter<AdapterStreaming.Holder> {

    private List<ModelStreaming> modelStreaming;

    public AdapterStreaming(List<ModelStreaming> modelStreamings){
        this.modelStreaming = modelStreamings;
    }

    @NonNull
    @Override
    public AdapterStreaming.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_streaming, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterStreaming.Holder holder, int position) {
        final ModelStreaming streaming = modelStreaming.get(position);
        holder.textViewDari.setText(streaming.getId_login());
        holder.textViewJam.setText(streaming.getJam());
        holder.textViewPesan.setText(streaming.getPesan());
    }

    @Override
    public int getItemCount() {
        return modelStreaming.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        TextView textViewDari, textViewJam, textViewPesan;

        Holder(@NonNull View itemView) {
            super(itemView);
            textViewDari = itemView.findViewById(R.id.streaming_dari);
            textViewJam = itemView.findViewById(R.id.streaming_jam);
            textViewPesan = itemView.findViewById(R.id.streaming_pesan);
        }
    }
}
