package com.surampaksakosoy.ydig.Services;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.surampaksakosoy.ydig.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

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
        Log.e(TAG, "sendPushNotification: " + jsonObject.toString());
        try {
            JSONObject data = jsonObject.getJSONObject("data");
            String title = data.getString("title");
            String message = data.getString("message");
//            String imageUrl = data.getString("image");

            MyNotificationManager myNotificationManager = new MyNotificationManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            myNotificationManager.showSmallNotification(title,message,intent);
//            if (imageUrl.equals("null")){
//                myNotificationManager.showSmallNotification(title, message, intent);
//            } else {
//                myNotificationManager.showBigNotification(title, message, imageUrl, intent);
//            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendPushNotification: " + e);
        }
    }
}
