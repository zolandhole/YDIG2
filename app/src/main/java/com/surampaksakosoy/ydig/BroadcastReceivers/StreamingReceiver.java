package com.surampaksakosoy.ydig.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StreamingReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String param = intent.getAction();
        if (param.equals("stop")){
            context.sendBroadcast(new Intent("stop"));
        } else if (param.equals("start")){
            context.sendBroadcast(new Intent("start"));
        }
    }
}
