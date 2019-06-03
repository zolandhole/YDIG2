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
import com.surampaksakosoy.ydig.utils.PublicAddress;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HandlerServer {
    private Context context;
    private String aktifitas;
    private static final String TAG = "HandlerServer";

    public HandlerServer(Context context, String aktifitas) {
        this.context = context;
        this.aktifitas = aktifitas;
    }

    public void getList(final VolleyCallback callback, final List<String> list) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, alamatServer(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optString("error").equals("true")) {
                                callback.onFailed(jsonObject.getString("pesan"));
                            } else {
                                JSONArray jsonArray = jsonObject.getJSONArray("pesan");
                                callback.onSuccess(jsonArray);

                            }
                        } catch (JSONException e) {
                            callback.onFailed(String.valueOf(e));
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (aktifitas.equals("GET_HOME_DATA") || aktifitas.equals("GET_MORE_DATA")) {
                            FragmentHome.resultError();
                        }
                        Log.e(TAG, "onErrorResponse: " + error);
                        if (String.valueOf(error).equals("com.android.volley.TimeoutError")){
                            Toast.makeText(context, "Koneksi internet terdeteksi lambat", Toast.LENGTH_SHORT).show();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
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
        switch (aktifitas) {
            case "LOGIN_DATA":
                URL = PublicAddress.POST_LOGIN;
                break;
            case "MAIN_LOGOUT":
                URL = PublicAddress.POST_LOGOUT;
                break;
            case "MAIN_SAVE_PHOTO":
                URL = PublicAddress.POST_SAVE_PHOTO;
                break;
            case "GET_HOME_DATA":
                URL = PublicAddress.GET_HOME_DATA;
                break;
            case "GET_MORE_DATA":
                URL = PublicAddress.GET_MORE_DATA;
                break;
            case "GET_PANDUAN":
                URL = PublicAddress.GET_PANDUAN;
                break;
            case "GET_UPDATE":
                URL = PublicAddress.GET_UPDATE;
                break;
        }
        return URL;
    }
}
