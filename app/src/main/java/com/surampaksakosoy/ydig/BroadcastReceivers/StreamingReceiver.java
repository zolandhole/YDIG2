package com.surampaksakosoy.ydig.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StreamingReceiver extends BroadcastReceiver {
//    private static final String TAG = "StreamingReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        String param = intent.getAction();
        if (param!= null){
            switch (param) {
                case "stop":
                    context.sendBroadcast(new Intent("stop"));
                    break;
                case "start":
                    context.sendBroadcast(new Intent("start"));
                    break;
                case "exit":
                    context.sendBroadcast(new Intent("exit"));
                    break;
            }
        }

    }
}
