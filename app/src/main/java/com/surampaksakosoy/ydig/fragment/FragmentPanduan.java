package com.surampaksakosoy.ydig.fragment;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
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
    private static final String TAG = "FragmentPanduan";
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

        requestMultiplePermission();
        getData();
        return view;
    }

    private void requestMultiplePermission() {
        Dexter.withActivity(getActivity()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity().getApplicationContext(), "Ada Kesalahan", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread().check();
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
                    Log.e(TAG, "onFailed: ");
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    resultSuccess(jsonArray);
                }
            }, list);
        }
    }

    private void resultSuccess(JSONArray jsonArray) {
        Log.e(TAG, "resultSuccess: ");
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject dataServer = jsonArray.getJSONObject(i);
                JSONObject isiData = dataServer.getJSONObject("data");
                ModelPanduan item = new ModelPanduan(
                        Integer.parseInt(dataServer.getString("id")),
                        isiData.getString("judul"),
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
            GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            AdapterPanduan adapterPanduan = new AdapterPanduan(getActivity().getApplicationContext(), modelPanduans);
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
