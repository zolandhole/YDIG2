package com.surampaksakosoy.ydig.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.surampaksakosoy.ydig.R;
import com.surampaksakosoy.ydig.adapters.AdapterHome;
import com.surampaksakosoy.ydig.handlers.ServerHandler;
import com.surampaksakosoy.ydig.models.ModelHome;
import com.surampaksakosoy.ydig.utils.PublicAddress;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FragmentHome extends Fragment {

    private List<ModelHome> list;
    private static final String TAG = "FragmentHome";

    public FragmentHome(){
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        List<String> parameter = new ArrayList<>();
        parameter.add("1");
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
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, PublicAddress.POST_HOME_GET_DATA,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            if (jsonObject.optString("error").equals("true")){
//                                Log.e(TAG, "onResponse: atas" + jsonObject.getString("pesan"));
//                            } else {
//                                JSONArray jsonArray = jsonObject.getJSONArray("pesan");
//                                list = new ArrayList<>();
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject dataServer = jsonArray.getJSONObject(i);
//                                    JSONObject dataIsi = dataServer.getJSONObject("data");
//                                    list.add(new ModelHome(
//                                            Integer.parseInt(dataIsi.getString("type")),
//                                            dataIsi.getString("data"),
//                                            dataIsi.getString("videoPath"),
//                                            dataIsi.getString("judul"),
//                                            dataIsi.getString("kontent"),
//                                            dataIsi.getString("arab"),
//                                            dataIsi.getString("arti")
//                                    ));
//                                }
//                                pasangkanKeRecyclerView(view);
//                            }
//                        } catch (JSONException e) {
//                            Log.e(TAG, "onResponse: " + e);
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onErrorResponse: " + error);
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("params", String.valueOf(parameter));
//                return params;
//            }
//        };
//        @SuppressLint({"NewApi", "LocalSuppress"}) RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(getActivity()));
//        requestQueue.add(stringRequest);
    }

    public void pasangkanKeRecyclerView(List<ModelHome> lists, View view) {
        list = lists;

        assert this.getArguments() != null;
        AdapterHome adapterHome = new AdapterHome(this.list,getActivity());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        RecyclerView recyclerView = view.findViewById(R.id.home_recyclerview);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapterHome);
    }
}
