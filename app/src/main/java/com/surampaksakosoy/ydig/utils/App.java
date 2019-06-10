package com.surampaksakosoy.ydig.utils;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Collections;

public class App extends Application {
    public static final String CHANNEL_ID = "radionotification";

    @Override
    public void onCreate() {
        super.onCreate();
        creteNotification();
    }

    private void creteNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel radioChannel = new NotificationChannel(
                    CHANNEL_ID, "Notifikasi Radio", NotificationManager.IMPORTANCE_LOW
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannels(Collections.singletonList(radioChannel));
        }
    }
}
