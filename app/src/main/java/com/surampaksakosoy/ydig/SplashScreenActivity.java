package com.surampaksakosoy.ydig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.surampaksakosoy.ydig.dbpanduan.DBKategori;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {
    private int mLoading = 0;
    private Handler handler = new Handler();
    private static final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        final ImageView imageView = findViewById(R.id.imageViewLogoSplash);
        final ProgressBar progressBar = findViewById(R.id.progressBarSplash);

        Animation fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top);
        imageView.setAnimation(fromTop);

        Drawable drawable = getResources().getDrawable(R.drawable.customprogressbar);
        progressBar.setProgressDrawable(drawable);

        getPanduanOffline();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mLoading < 100){
                    mLoading++;
                    android.os.SystemClock.sleep(50);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(mLoading);
                        }
                    });
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imageView.animate().alpha(0.0f).setDuration(1000).translationY(imageView.getHeight()).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }
                        progressBar.animate().alpha(0.0f).setDuration(800).translationY(imageView.getHeight());
                    }
                });
            }
        }).start();
    }

    private void getPanduanOffline() {
        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(SplashScreenActivity.this, "GET_PANDUAN");
        synchronized (SplashScreenActivity.this){
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    Log.e(TAG, "onFailed: FAILED CONNECT TO SERVER");
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    resultSuccess(jsonArray);
                }
            }, list);
        }
    }

    private void resultSuccess(JSONArray jsonArray) {
        final DBKategori dbKategori = new DBKategori(SplashScreenActivity.this);
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                final JSONObject dataServer = jsonArray.getJSONObject(i);
                final JSONObject isiData = dataServer.getJSONObject("data");
                Glide.with(this)
                        .asBitmap()
                        .load(isiData.getString("image_path"))
                        .centerCrop()
                        .into(new SimpleTarget<Bitmap>(200,200) {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                byte[] data = baos.toByteArray();
                                Log.e(TAG, "onResourceReady: " + Arrays.toString(data));
                                try {
                                    dbKategori.addToDb(dataServer.getString("id"),isiData.getString("judul"), data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "resultSuccess: " + e);
        }
    }
}
