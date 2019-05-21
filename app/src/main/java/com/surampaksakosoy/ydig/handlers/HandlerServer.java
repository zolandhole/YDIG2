package com.surampaksakosoy.ydig.handlers;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.surampaksakosoy.ydig.models.ModelHome;
import com.surampaksakosoy.ydig.utils.PublicAddress;
import com.surampaksakosoy.ydig.utils.VolleyCallback;

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
                                List<ModelHome> list = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject dataServer = jsonArray.getJSONObject(i);
                                    JSONObject dataIsi = dataServer.getJSONObject("data");
                                    list.add(new ModelHome(
                                            Integer.parseInt(dataIsi.getString("type")),
                                            dataIsi.getString("data"),
                                            dataIsi.getString("videoPath"),
                                            dataIsi.getString("judul"),
                                            dataIsi.getString("kontent"),
                                            dataIsi.getString("arab"),
                                            dataIsi.getString("arti")
                                    ));
                                    Log.e(TAG, "onResponse: " + dataIsi.getString("arab"));
                                }
                                callback.onSuccess(list);
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
        }
        return URL;
    }
}
