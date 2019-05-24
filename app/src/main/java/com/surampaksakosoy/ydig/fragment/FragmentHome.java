package com.surampaksakosoy.ydig.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterHome;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.OnLoadMoreListener;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;
import com.surampaksakosoy.ydig.models.ModelHomeJadi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class FragmentHome extends Fragment {

    private TextView textView;
    private RecyclerView recyclerView;
    private AdapterHome adapterHome;
    private String lastID;
    private List<ModelHomeJadi> modelHomes;
    private static final String TAG = "FragmentHome";
    private FragmentHomeListener listener;

    public FragmentHome(){
    }

    public interface  FragmentHomeListener{
        void onInputHomeSent(String input);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentHome.FragmentHomeListener){
            listener = (FragmentHomeListener) context;
        } else {
            throw new RuntimeException(context.toString() + " harus mengimplementasi FragmentHomeListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recyclerview);
        textView = view.findViewById(R.id.home_textview);

        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(getActivity().getApplicationContext(),"GET_HOME_DATA");
        synchronized (getActivity().getApplicationContext()){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    Log.e(TAG, "onFailed: " + result);
                }

                @Override
                public void onSuccess(List<ModelHomeJadi> result, String lastID) {
                    tampilankanSuccess(result, lastID);
                }

                @Override
                public void onJsonArray(JSONArray jsonArray) {

                }
            }, list);
        }


        return view;
    }

    private void tampilankanSuccess(final List<ModelHomeJadi> result, final String lastID) {
        this.lastID = lastID;
        this.modelHomes = result;
        if (result.isEmpty()){
            textView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            textView.setVisibility(View.GONE);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            recyclerView.setLayoutManager(linearLayoutManager);
            adapterHome = new AdapterHome(modelHomes,getActivity().getApplicationContext(), recyclerView);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapterHome);
            recyclerView.setVisibility(View.VISIBLE);

            adapterHome.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    modelHomes.add(null);
                    adapterHome.notifyItemInserted(modelHomes.size()-1);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            modelHomes.remove(modelHomes.size()-1);
                            adapterHome.notifyItemRemoved(modelHomes.size());
                            loadMoreData();

                        }
                    }, 2000);
                }
            });
            Log.e(TAG, "tampilankanSuccess: " + lastID);
        }

    }

    private void loadMoreData() {
//        listener.onInputHomeSent(lastID);
        List<String> params = new ArrayList<>();
        params.add(lastID);
            HandlerServer handlerServer = new HandlerServer(getActivity(),"GET_MORE_DATA");
            synchronized (getActivity()){
                handlerServer.getList(new VolleyCallback() {
                    @Override
                    public void onFailed(String result) {
                    }

                    @Override
                    public void onSuccess(List<ModelHomeJadi> result, String lastID) {
                    }

                    @Override
                    public void onJsonArray(JSONArray jsonArray) {
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
                            Log.e(TAG, "onJsonArray: " + lastID);
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, params);
            }
    }

    public void updateData(List<ModelHomeJadi> newData){
        Log.e(TAG, "updateData: " + newData);
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
