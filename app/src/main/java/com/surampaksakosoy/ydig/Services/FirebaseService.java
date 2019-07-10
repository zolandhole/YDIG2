package com.surampaksakosoy.ydig.Services;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseService";

    @Override
    public void onCreate() {

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
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);

            String title = data.getString("title");
            String message = data.getString("message");

            myNotificationManager.showStreamingNotification(title,message);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "sendPushNotification: " + e);
        }
    }
}
