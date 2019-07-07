package com.surampaksakosoy.ydig.Services;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.surampaksakosoy.ydig.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";
    private LocalBroadcastManager broadcastManager;

    @Override
    public void onCreate() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "onMessageReceived: Data Payload: " + remoteMessage.getData().toString());
        if (remoteMessage.getData().size() > 0){
            try {
                JSONObject jsonObject = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendPushNotification(JSONObject jsonObject) {
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            if (data.has("typeNotif")){
                String typeNotif = data.getString("typeNotif");
                if (typeNotif.equals("broadcastRadio")){
                    Intent i = new Intent("MyData");
                    i.putExtra("streamingRadio", typeNotif);
                    broadcastManager.sendBroadcast(i);
                }
            }

            String title = data.getString("title");
            String message = data.getString("message");

            myNotificationManager.showStreamingNotification(title,message,intent);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendPushNotification: " + e);
        }
    }
}
