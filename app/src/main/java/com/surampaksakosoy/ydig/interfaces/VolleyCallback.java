package com.surampaksakosoy.ydig.interfaces;

import org.json.JSONArray;

public interface VolleyCallback {
    void onFailed(String result);
    void onSuccess(JSONArray jsonArray);
}
