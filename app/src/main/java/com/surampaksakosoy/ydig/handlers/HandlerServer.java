package com.surampaksakosoy.ydig.handlers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.surampaksakosoy.ydig.fragment.FragmentHome;
import com.surampaksakosoy.ydig.models.ModelHomeJadi;
import com.surampaksakosoy.ydig.utils.PublicAddress;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerServer {
    private static final String TAG = "HandlerServer";
    private Context context;
    private String aktifitas;

    public HandlerServer(Context context, String aktifitas){
        this.context = context;
        this.aktifitas = aktifitas;
    }

    public void getList(final VolleyCallback callback, final List<String> list){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, alamatServer(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("error").equals("true")){
                                callback.onFailed(jsonObject.getString("pesan"));
                                Log.e(TAG, "responseFromServer: " + jsonObject.getString("pesan") );
                            } else {
                                JSONArray jsonArray = jsonObject.getJSONArray("pesan");
                                switch (aktifitas){
                                    case "GET_HOME_DATA":
                                        List<ModelHomeJadi> list = new ArrayList<>();
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
                                        String lastID = dataServer.getString("id");
                                        callback.onSuccess(list, lastID);
                                        break;

                                    case "GET_MORE_DATA":
                                        callback.onJsonArray(jsonArray);
                                        break;
                                }

                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "responseFromServer: "+ e);
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error);
                        if (aktifitas.equals("GET_HOME_DATA") || aktifitas.equals("GET_MORE_DATA")){
                            FragmentHome.resultError();
                        }
                        Toast.makeText(context, "Tidak ada koneksi Internet", Toast.LENGTH_SHORT).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<>();
                params.put("params", String.valueOf(list));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    private String alamatServer() {
        String URL = "";
        switch (aktifitas){
            case "LOGIN_DATA": URL = PublicAddress.POST_LOGIN; break;
            case "MAIN_LOGOUT": URL = PublicAddress.POST_LOGOUT; break;
            case "MAIN_SAVE_PHOTO": URL = PublicAddress.POST_SAVE_PHOTO; break;
            case "GET_HOME_DATA": URL = PublicAddress.GET_HOME_DATA; break;
            case "GET_MORE_DATA": URL = PublicAddress.GET_MORE_DATA; break;
        }
        return URL;
    }
}
