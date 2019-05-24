package com.surampaksakosoy.ydig.interfaces;

import com.surampaksakosoy.ydig.models.ModelHomeJadi;

import org.json.JSONArray;

import java.util.List;

public interface VolleyCallback {
    void onFailed(String result);
    void onSuccess(List<ModelHomeJadi> result, String lastID);
    void onJsonArray(JSONArray jsonArray);
}
