package com.surampaksakosoy.ydig.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterHome;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.OnLoadMoreListener;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;
import com.surampaksakosoy.ydig.models.ModelHomeJadi;
import com.surampaksakosoy.ydig.utils.NoInternetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private TextView textView;
    private RecyclerView recyclerView;
    private AdapterHome adapterHome;
    private String lastID;
    private List<ModelHomeJadi> modelHomes;
    private ProgressBar progressBar;
    @SuppressLint("StaticFieldLeak")
    private static SwipeRefreshLayout swipeRefresh;
    private FragmentHomeListener listener;
    private NoInternetConnection internetConnection;

    public FragmentHome() {
    }

    public static void resultError() {
        swipeRefresh.setRefreshing(false);
    }

    public interface FragmentHomeListener {
        void onInputHomeSent(String input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHome.FragmentHomeListener) {
            listener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " harus mengimplementasi FragmentHomeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recyclerview);
        textView = view.findViewById(R.id.home_textview);
        progressBar = view.findViewById(R.id.home_progressbar);
        swipeRefresh = view.findViewById(R.id.home_parent_refresh);
        internetConnection = new NoInternetConnection(getActivity().getApplicationContext());
        swipeRefresh.setOnRefreshListener(this);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_blue_dark, android.R.color.holo_orange_dark, android.R.color.holo_green_dark
        );
        swipeRefresh.post(new Runnable() {
            @Override
            public void run() {
                if (internetConnection.isNetworkAvailable()) {
                    swipeRefresh.setRefreshing(true);
                    getData();
                } else {
                    tidakAdaData();
                }
            }
        });

        listener.onInputHomeSent("home");
        progressBar.setVisibility(View.VISIBLE);
        return view;
    }

    private void getData() {
        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(), "GET_HOME_DATA");
        synchronized (getActivity().getApplicationContext()) {
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    tidakAdaData();
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    prosessResult(jsonArray);
                }
            }, list);
        }
    }

    private void prosessResult(JSONArray jsonArray) {
        List<ModelHomeJadi> list = new ArrayList<>();
        try {
            JSONObject dataServer = null;
            for (int i = 0; i < jsonArray.length(); i++) {
                dataServer = jsonArray.getJSONObject(i);
                JSONObject isiData = dataServer.getJSONObject("data");
                list.add(new ModelHomeJadi(
                        Integer.parseInt(dataServer.getString("id")),
                        Integer.parseInt(isiData.getString("type")),
                        isiData.getString("data"),
                        isiData.getString("videoPath"),
                        isiData.getString("judul"),
                        isiData.getString("kontent"),
                        isiData.getString("arab"),
                        isiData.getString("arti"),
                        dataServer.getString("upload_date")
                ));
            }
            lastID = dataServer.getString("id");
            tampilankanSuccess(list);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void tidakAdaData() {
        progressBar.setVisibility(View.GONE);
    }

    private void tampilankanSuccess(List<ModelHomeJadi> result) {
        this.modelHomes = result;
        if (result.isEmpty()) {
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            adapterHome = new AdapterHome(modelHomes, getActivity().getApplicationContext(), recyclerView);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapterHome);
            recyclerView.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);

            adapterHome.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (internetConnection.isNetworkAvailable()) {
                        modelHomes.add(null);
                        adapterHome.notifyItemInserted(modelHomes.size() - 1);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                modelHomes.remove(modelHomes.size() - 1);
                                adapterHome.notifyItemRemoved(modelHomes.size());
                                loadMoreData();
                            }
                        }, 1000);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Tidak ada koneksi Internet", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void loadMoreData() {
        List<String> params = new ArrayList<>();
        params.add(lastID);
        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(), "GET_MORE_DATA");
        if (getActivity().getApplicationContext() != null) {
            synchronized (getActivity()) {
                handlerServer.getList(new VolleyCallback() {
                    @Override
                    public void onFailed(String result) {
                        tidakAdaData();
                        Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(JSONArray jsonArray) {
                        try {
                            JSONObject dataServer = null;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                dataServer = jsonArray.getJSONObject(i);
                                JSONObject isiData = dataServer.getJSONObject("data");
                                ModelHomeJadi item = (new ModelHomeJadi(
                                        Integer.parseInt(dataServer.getString("id")),
                                        Integer.parseInt(isiData.getString("type")),
                                        isiData.getString("data"),
                                        isiData.getString("videoPath"),
                                        isiData.getString("judul"),
                                        isiData.getString("kontent"),
                                        isiData.getString("arab"),
                                        isiData.getString("arti"),
                                        dataServer.getString("upload_date")
                                ));
                                modelHomes.add(item);
                            }
                            adapterHome.notifyDataSetChanged();
                            adapterHome.setLoaded();
                            lastID = dataServer.getString("id");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, params);
            }
        }
    }

    @Override
    public void onRefresh() {
        getData();
    }

//    public void updateData(List<ModelHomeJadi> newData){
//        Log.e(TAG, "updateData: " + newData);
//    }

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
