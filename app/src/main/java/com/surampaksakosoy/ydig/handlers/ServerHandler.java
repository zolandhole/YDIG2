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
import com.surampaksakosoy.ydig.LoginActivity;
import com.surampaksakosoy.ydig.utils.PublicAddress;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerHandler {

    private static final String TAG = "ServerHandler";
    private Context context;
    private String aktifitas;

    public ServerHandler(Context context, String aktifitas){
        this.context = context;
        this.aktifitas = aktifitas;
    }

    public void sendData(final List<String> list){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, alamatServer(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        responseFromServer(response);
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

    private void responseFromServer(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString("error").equals("true")){
                Log.e(TAG, "responseFromServer: " + jsonObject.getString("pesan") );
            } else {
                responseSuccess(jsonObject);
            }
        } catch (JSONException e) {
            Log.e(TAG, "responseFromServer: "+ e);
            e.printStackTrace();
        }
    }

    private void responseSuccess(JSONObject jsonObject) {
        if ("LOGIN_DATA".equals(aktifitas)) {
            LoginActivity loginActivity = (LoginActivity) context;
            loginActivity.keMainActivity();
        } else {
            try {
                Toast.makeText(context, jsonObject.getString("pesan"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Log.e(TAG, "responseSuccess: " + e);
                e.printStackTrace();
            }
        }
    }

    private String alamatServer() {
        String URL = "";
        switch (aktifitas){
            case "LOGIN_DATA": URL = PublicAddress.POST_LOGIN; break;
            case "MAIN_LOGOUT": URL = PublicAddress.POST_LOGOUT; break;
        }
        return URL;
    }
}
