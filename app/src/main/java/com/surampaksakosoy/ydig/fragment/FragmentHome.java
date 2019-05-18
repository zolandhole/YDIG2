package com.surampaksakosoy.ydig.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterHome;
import com.surampaksakosoy.ydig.handlers.ServerHandler;
import com.surampaksakosoy.ydig.models.ModelHome;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private List<ModelHome> list;
    private RecyclerView recyclerView;
    private final int THREAD_SHOT = 5;
    private boolean isLoading = false;
    private static final String TAG = "FragmentHome";

    public FragmentHome(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        List<String> parameter = new ArrayList<>();
        parameter.add("6");
        getDataFromServer(parameter, view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void getDataFromServer(final List<String> parameter, final View view) {
        ServerHandler serverHandler = new ServerHandler(getActivity(),"HOME_GET_DATA");
        synchronized (getActivity()){
            serverHandler.kirimData(parameter, view);
        }
    }

    public void pasangkanKeRecyclerView(List<ModelHome> lists, View view) {
        list = lists;

        assert this.getArguments() != null;
        AdapterHome adapterHome = new AdapterHome(this.list,getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView = view.findViewById(R.id.home_recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterHome);

        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (!isLoading && layoutManager.getItemCount() - THREAD_SHOT == layoutManager.findLastVisibleItemPosition()){
                    loadMoreData();
                }
            }
        });
    }

    private void loadMoreData() {
        List<String> parameter = new ArrayList<>();
        parameter.add("1");
        parameter.add("6");
    }
}
