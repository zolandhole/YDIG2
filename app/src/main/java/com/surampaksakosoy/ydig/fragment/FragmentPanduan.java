package com.surampaksakosoy.ydig.fragment;

import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterPanduan;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;
import com.surampaksakosoy.ydig.models.ModelPanduan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentPanduan extends Fragment {

    private TextView textView;
    private RecyclerView recyclerView;
    private List<ModelPanduan> modelPanduans;
    private ProgressBar progressBar;
    private FragmentPanduanListener listener;

    public FragmentPanduan(){
    }

    public interface FragmentPanduanListener {
        void onInputPanduanSent(String input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentPanduan.FragmentPanduanListener){
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
        getData();
        return view;
    }

    private void getData() {
        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(), "GET_PANDUAN");
        synchronized (getActivity().getApplicationContext()){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    textView.setText(result);
                    textView.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    resultSuccess(jsonArray);
                }
            }, list);
        }
    }

    private void resultSuccess(JSONArray jsonArray) {
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject dataServer = jsonArray.getJSONObject(i);
                JSONObject isiData = dataServer.getJSONObject("data");
                ModelPanduan item = new ModelPanduan(
                        Integer.parseInt(dataServer.getString("id")),
                        isiData.getString("judul"),
                        isiData.getString("deskripsi"),
                        isiData.getString("image_path"),
                        dataServer.getString("upload_date"),
                        Integer.parseInt(dataServer.getString("status"))
                );
                modelPanduans.add(item);
            }
            tampilkanKeRecyclerView();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tampilkanKeRecyclerView() {
        if (modelPanduans.isEmpty()){
            textView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            AdapterPanduan adapterPanduan = new AdapterPanduan(getActivity().getApplicationContext(), modelPanduans);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
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
