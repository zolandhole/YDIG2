package com.surampaksakosoy.ydig.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterPanduan;
import com.surampaksakosoy.ydig.dbpanduan.DBKategori;
import com.surampaksakosoy.ydig.models.ModelPanduan;

import java.util.ArrayList;
import java.util.List;

public class FragmentPanduan extends Fragment {
//    private static final String TAG = "FragmentPanduan";
    private TextView textView;
    private RecyclerView recyclerView;
    private List<ModelPanduan> modelPanduans;
    private ProgressBar progressBar;
    private FragmentPanduanListener listener;


    public FragmentPanduan() {
    }

    public interface FragmentPanduanListener {
        void onInputPanduanSent(String input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentPanduan.FragmentPanduanListener) {
            listener = (FragmentPanduanListener) context;
        } else {
            throw new RuntimeException(context.toString() + "harus mengimplementasi FragmentPanduanListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_panduan, container, false);
        textView = view.findViewById(R.id.panduan_textview);
        textView.setText(R.string.arab_here);
        textView.setVisibility(View.GONE);
        modelPanduans = new ArrayList<>();
        recyclerView = view.findViewById(R.id.panduan_recyclerview);
        progressBar = view.findViewById(R.id.panduan_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        listener.onInputPanduanSent("panduan");
        getDataFromDB();
        return view;
    }

    private void getDataFromDB() {
        DBKategori dbKategori = new DBKategori(getActivity().getApplicationContext());
        Cursor cursor = dbKategori.getData("SELECT * FROM tabelkategori");
        while (cursor.moveToNext()){
            int id = cursor.getInt(0);
            String panduan_id = cursor.getString(2);
            String judul = cursor.getString(3);
            byte[] image = cursor.getBlob(4);
            ModelPanduan item = new ModelPanduan(
                    id,panduan_id,judul,image
            );
            modelPanduans.add(item);
        }
        tampilkanKeRecyclerView();
    }

    private void tampilkanKeRecyclerView() {
        if (modelPanduans.isEmpty()){
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {

            textView.setVisibility(View.GONE);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            AdapterPanduan adapterPanduan = new AdapterPanduan(modelPanduans, getActivity().getApplicationContext());
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapterPanduan);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}
