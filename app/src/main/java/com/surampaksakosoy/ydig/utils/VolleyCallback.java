package com.surampaksakosoy.ydig.utils;

import com.surampaksakosoy.ydig.models.ModelHome;

import java.util.List;

public interface VolleyCallback {
    void onFailed(String result);
    void onSuccess(List<ModelHome> result);
}
