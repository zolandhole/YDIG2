package com.surampaksakosoy.ydig;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.androidstudy.networkmanager.Monitor;
import com.androidstudy.networkmanager.Tovuti;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.snackbar.Snackbar;
import com.surampaksakosoy.ydig.dbpanduan.DBKategori;
import com.surampaksakosoy.ydig.handlers.HandlerServer;
import com.surampaksakosoy.ydig.interfaces.VolleyCallback;
import com.surampaksakosoy.ydig.utils.NoInternetConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {
    private int mLoading = 0, progress = 0, rowDB;
    private Handler handler = new Handler();
    private DBKategori dbKategori;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView textViewProgress;
    private String aktifitas, versi;
    private Snackbar snackbar;
    private boolean doubleCheck = true;
    private static final String TAG = "SplashScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        imageView = findViewById(R.id.imageViewLogoSplash);
        progressBar = findViewById(R.id.progressBarSplash);
        textViewProgress = findViewById(R.id.splash_progress);

        Animation fromTop = AnimationUtils.loadAnimation(this, R.anim.from_top);
        imageView.setAnimation(fromTop);
        Drawable drawable = getResources().getDrawable(R.drawable.customprogressbar);
        progressBar.setProgressDrawable(drawable);
        progressBar.setVisibility(View.GONE);
        dbKategori = new DBKategori(SplashScreenActivity.this);
        rowDB = dbKategori.countKategori();

        snackbar = Snackbar.make(findViewById(R.id.splash_layout),
                "Membutuhkan koneksi internet",
                Snackbar.LENGTH_INDEFINITE);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkInternetConnction();
            }
        }, 2500);
    }

    @Override
    protected void onStop() {
        Tovuti.from(SplashScreenActivity.this).stop();
        super.onStop();
    }

    private void checkInternetConnction() {
        NoInternetConnection internetConnection = new NoInternetConnection(SplashScreenActivity.this);
        if (internetConnection.isNetworkAvailable()) {
            checkUpdate();
        } else {
            if (rowDB > 0){
                loadingFinish();
            } else {
                Tovuti.from(SplashScreenActivity.this).monitor(new Monitor.ConnectivityListener() {
                    @Override
                    public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
                        if (isConnected){
                            snackbar.dismiss();
                            checkInternetConnction();
                        } else {

                            View sbView = snackbar.getView();
                            sbView.setBackgroundColor(ContextCompat.getColor(SplashScreenActivity.this, R.color.merahMarun));
                            snackbar.show();
                        }
                    }
                });
            }
        }
    }

    private void checkUpdate() {
        aktifitas = "GET_UPDATE";
        List<String> list = new ArrayList<>();
        list.add("0");
        HandlerServer handlerServer = new HandlerServer(SplashScreenActivity.this, aktifitas);
        synchronized (SplashScreenActivity.this) {
            handlerServer.getList(new VolleyCallback() {
                @Override
                public void onFailed(String result) {
                    if (doubleCheck) {
                        checkInternetConnction();
                        doubleCheck = false;
                    }
                    Log.e(TAG, "onFailed: " + result + " double check :" + doubleCheck);
                }

                @Override
                public void onSuccess(JSONArray jsonArray) {
                    resultFromServer(jsonArray);
                }
            }, list);
        }
    }

    private void resultFromServer(final JSONArray jsonArray) {

        rowDB = dbKategori.countKategori();

        switch (aktifitas){
            case "GET_UPDATE":
                JSONObject dataServer, isiData;
                try {
                    dataServer = jsonArray.getJSONObject(0);
                    isiData = dataServer.getJSONObject("data");
                    versi = isiData.getString("kategori");

                    if (rowDB < 1){
                        updateDataFromServer();
                    } else {
                        Cursor cursor = dbKategori.getData("SELECT * FROM tabelkategori");
                        String versiKategori = "";
                        if (cursor.moveToNext()) {
                            versiKategori = cursor.getString(1);
                        }

                        if (versiKategori.equals(versi)){
                            loadingFinish();
                        } else {
                            updateDataFromServer();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "resultFromServer: JSONException: " + e);
                }
                break;
            case "GET_PANDUAN":
                updateText("Menyiapkan Data Panduan");
                progress = 15;
                updateProgressBar(progress);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                final JSONObject dataServer = jsonArray.getJSONObject(i);
                                final JSONObject isiData = dataServer.getJSONObject("data");
                                Glide.with(getApplicationContext())
                                        .asBitmap()
                                        .load(isiData.getString("image_path"))
                                        .centerCrop()
                                        .into(new SimpleTarget<Bitmap>(200, 200) {
                                            @Override
                                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                                byte[] data = baos.toByteArray();
                                                try {
                                                    dbKategori.addToDb(versi, dataServer.getString("id"), isiData.getString("judul"), data);
                                                    updateText("Memuat Kategori Panduan");
                                                    progress = 100;
                                                    updateProgressBar(progress);
                                                } catch (JSONException e) {
                                                    Log.e(TAG, "onResourceReady: JSONException: " + e);
                                                    e.printStackTrace();
                                                }
                                            }
                                        });
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                            Log.e(TAG, "resultFromServer: JSONEXception: " + e);
                        }
                    }
                }, 100);
                break;
        }
    }

    private void updateDataFromServer() {
        progressBar.setVisibility(View.VISIBLE);
        textViewProgress.setVisibility(View.VISIBLE);
        updateText("Menyiapkan Data");
        progress = 10;
        updateProgressBar(progress);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dbKategori.deleteDB();
                aktifitas = "GET_PANDUAN";
                List<String> list = new ArrayList<>();
                list.add("0");
                HandlerServer handlerServer = new HandlerServer(SplashScreenActivity.this, aktifitas);
                synchronized (SplashScreenActivity.this){
                    handlerServer.getList(new VolleyCallback() {
                        @Override
                        public void onFailed(String result) {
                            if (doubleCheck){
                                updateDataFromServer();
                                doubleCheck = false;
                            }
                        }

                        @Override
                        public void onSuccess(JSONArray jsonArray) {
                            resultFromServer(jsonArray);
                        }
                    }, list);
                }
            }
        }, 100);

    }

    //
//    private void checkInternetConnction() {
//        Tovuti.from(this).monitor(new Monitor.ConnectivityListener() {
//            @Override
//            public void onConnectivityChanged(int connectionType, boolean isConnected, boolean isFast) {
//                if (isConnected) {
//                    checkUpdate();
//                } else {
//                    if (rowDB > 0) {
//                        loadingFinish();
//                    } else {
//                        Snackbar.make(findViewById(R.id.splash_layout), "Data Offline kosong, membutuhkan koneksi internet untuk memuat data", Snackbar.LENGTH_LONG).show();
//                        progressBar.setVisibility(View.GONE);
//                        finish();
//                    }
//                }
//            }
//        });
//    }
//
//    private void checkUpdate() {
//        aktifitas = "GET_UPDATE";
//        List<String> list = new ArrayList<>();
//        list.add("0");
//        HandlerServer handlerServer = new HandlerServer(SplashScreenActivity.this, aktifitas);
//        synchronized (SplashScreenActivity.this) {
//            handlerServer.getList(new VolleyCallback() {
//                @Override
//                public void onFailed(String result) {
//                    Log.e(TAG, "onFailed: +  Gagal memuat data");
//                }
//
//                @Override
//                public void onSuccess(JSONArray jsonArray) {
//                    resultSuccess(jsonArray);
//                }
//            }, list);
//        }
//    }
//
//    public void getPanduanOffline() {
//        aktifitas = "GET_PANDUAN";
//        dbKategori.deleteDB();
//        List<String> list = new ArrayList<>();
//        list.add("0");
//        HandlerServer handlerServer = new HandlerServer(SplashScreenActivity.this, aktifitas);
//        synchronized (SplashScreenActivity.this) {
//            handlerServer.getList(new VolleyCallback() {
//                @Override
//                public void onFailed(String result) {
//                    Log.e(TAG, "onFailed: FAILED CONNECT TO SERVER");
//                }
//
//                @Override
//                public void onSuccess(JSONArray jsonArray) {
//                    progress = 100;
//                    updateProgressBar(progress);
//                    resultSuccess(jsonArray);
//                }
//            }, list);
//        }
//    }
//
//    private void resultSuccess(JSONArray jsonArray) {
//        try {
//            JSONObject dataServer;
//            JSONObject isiData;
//            switch (aktifitas) {
//                case "GET_UPDATE":
//                    dataServer = jsonArray.getJSONObject(0);
//                    isiData = dataServer.getJSONObject("data");
//                    versi = isiData.getString("kategori");
//                    Log.e(TAG, "resultSuccess: " + rowDB);
//                    if (rowDB > 0) {
//                        DBKategori dbKategori = new DBKategori(SplashScreenActivity.this);
//                        Cursor cursor = dbKategori.getData("SELECT * FROM tabelkategori");
//                        String versiKategori = "";
//                        if (cursor.moveToNext()) {
//                            versiKategori = cursor.getString(1);
//                            Log.e(TAG, "resultSuccess: MoveNext" + versiKategori);
//                        }
//                        Log.e(TAG, "resultSuccess: " + versiKategori);
//
//                        if (versiKategori.equals(versi)) {
//                            loadingFinish();
//                            Log.e(TAG, "resultSuccess: Versi software sama");
//                        } else {
//                            getPanduanOffline();
//                            Log.e(TAG, "resultSuccess: Anda Melawati Bagian Ini");
//                        }
//                    } else {
//                        getPanduanOffline();
//                    }
//                    break;
//                case "GET_PANDUAN":
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        mLoading = mLoading + 5;
//                        updateProgressBar(mLoading);
//                        dataServer = jsonArray.getJSONObject(i);
//                        isiData = dataServer.getJSONObject("data");
//                        final JSONObject finalIsiData = isiData;
//                        final JSONObject finalDataServer = dataServer;
//                        Glide.with(getApplicationContext())
//                                .asBitmap()
//                                .load(isiData.getString("image_path"))
//                                .centerCrop()
//                                .into(new SimpleTarget<Bitmap>(200, 200) {
//                                    @Override
//                                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
//                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                        resource.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                                        byte[] data = baos.toByteArray();
//                                        try {
//                                            dbKategori.addToDb(versi, finalDataServer.getString("id"), finalIsiData.getString("judul"), data);
//                                            Log.e(TAG, "onResourceReady: " + finalIsiData.getString("judul"));
//                                        } catch (JSONException e) {
//                                            Log.e(TAG, "onResourceReady: " + e);
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                });
//                    }
//                    break;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "resultSuccess: " + e);
//        }
//    }
//

    private void updateText(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewProgress.setText(text);
            }
        });
    }
    private void updateProgressBar(final int loading) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                while (mLoading < loading) {
                    mLoading++;
                    android.os.SystemClock.sleep(50);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setProgress(mLoading);
                        }
                    });
                }

                if (loading == 100) {
                    loadingFinish();
                }
            }
        }).start();
    }

    private void loadingFinish() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageView.animate().alpha(0.0f).setDuration(1000).translationY(imageView.getHeight()).withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
                progressBar.animate().alpha(0.0f).setDuration(800).translationY(imageView.getHeight());
            }
        });
    }
}
