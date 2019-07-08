package com.surampaksakosoy.ydig.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.text.HtmlCompat;
import com.surampaksakosoy.ydig.MainActivity;
import com.surampaksakosoy.ydig.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.surampaksakosoy.ydig.utils.App.CHANNEL_ID;

class MyNotificationManager {
    private static final int ID_BIG_NOTIFICATION = 234;
    private static final int ID_SMALL_NOTIFICATION = 235;

    private Context context;

    MyNotificationManager(Context context){
        this.context = context;
    }

    void showBigNotification(String title, String message, String url, Intent intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context,ID_BIG_NOTIFICATION,intent,PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
//            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
            bigPictureStyle.setSummaryText(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
            bigPictureStyle.bigPicture(getBitmapFromUrl(url));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            Notification notification;
            notification = builder
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setTicker(title).setWhen(0)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setStyle(bigPictureStyle)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ydig_notif))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(ID_BIG_NOTIFICATION, notification);
        } else {
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent;

            pendingIntent = PendingIntent.getActivity(context,666, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY).toString());
            bigPictureStyle.bigPicture(getBitmapFromUrl(url));
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification;
            notification = builder
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setTicker(title).setWhen(0)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_ydig_notif))
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(666, notification);
        }
    }

    private Bitmap getBitmapFromUrl(String url) {
        try {
            URL strURL = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) strURL.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void showStreamingNotification (String title, String message, Intent intent){
        Intent aint = new Intent(context, MainActivity.class);
        aint.putExtra("streamingRadio", "broadcastRadio");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            PendingIntent pendingIntent = PendingIntent.getActivity(context, ID_SMALL_NOTIFICATION, aint, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            Notification notification;
            notification = builder
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        } else {
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent;

            pendingIntent = PendingIntent.getActivity(context,556, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification;
            notification = builder
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ydig_notif))
                    .setContentText(message)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(555, notification);
        }
    }

    void showSmallNotification(String title, String message, Intent intent){
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("streamingRadio", "streamingRadio");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent pendingIntent = PendingIntent
                    .getActivity(context, ID_SMALL_NOTIFICATION, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
            Notification notification;
            notification = builder
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ydig_notif))
                    .setContentText(message)
                    .build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(ID_SMALL_NOTIFICATION, notification);
        } else {
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent;

            pendingIntent = PendingIntent.getActivity(context,555, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            Notification notification;
            notification = builder
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSmallIcon(R.drawable.ic_ydig_notif)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_ydig_notif))
                    .setContentText(message)
                    .build();
            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(555, notification);
        }
    }
}
