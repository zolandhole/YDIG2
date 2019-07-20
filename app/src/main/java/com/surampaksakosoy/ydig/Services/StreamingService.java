package com.surampaksakosoy.ydig.Services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.surampaksakosoy.ydig.BroadcastReceivers.StreamingReceiver;
import com.surampaksakosoy.ydig.MainActivity;
import com.surampaksakosoy.ydig.R;

import java.io.IOException;

import static com.surampaksakosoy.ydig.utils.App.CHANNEL_ID;

public class StreamingService extends Service implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPausedCall = false;
    private BroadcastReceiver broadcastReceiver;
    private static final String TAG = "StreamingService";

    @Override
    public void onCreate() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("stop");
        filter.addAction("start");
        filter.addAction("exit");
        filter.addAction("error");
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.e(TAG, "onReceive: " + action);
                assert  action != null;
                switch (action){
                    case "stop":
                        if (mediaPlayer != null){
                            pauseMedia();
                        }
                        break;
                    case "start":
                        playMedia();
                        break;
                    case "exit":
                        stopMedia();
                        break;
                }
            }
        };

        registerReceiver(broadcastReceiver, filter);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.reset();

        AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case (AudioManager.AUDIOFOCUS_LOSS):
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                        pauseMedia();
                        break;
                    case (AudioManager.AUDIOFOCUS_GAIN):
                        playMedia();
                        break;
                    case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                        mediaPlayer.setVolume(0.1f, 0.1f);
                        break;
                }
            }
        };

        AudioManager audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int mediaresult = audioManager.requestAudioFocus(onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (mediaresult == AudioManager.AUDIOFOCUS_REQUEST_GRANTED){
            Log.e(TAG, "onCreate: GRANTED");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initIfPhoneCall();
        String nama;
        if (intent!=null){
            nama = intent.getExtras().getString("name");
            showNotification(nama);
            mediaPlayer.reset();
            if (!mediaPlayer.isPlaying()){
                try {
                    mediaPlayer.setDataSource(intent.getExtras().getString("url"));
                    mediaPlayer.prepareAsync();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void playMedia(){
        if (!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void stopMedia(){
        if (mediaPlayer != null){
            if (mediaPlayer.isPlaying()){
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        stopForeground(true);
        stopSelf();
    }

    private void pauseMedia(){
        if (mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    private void initIfPhoneCall(){
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            isPausedCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (isPausedCall){
                                isPausedCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void showNotification(String name){
        Intent intentNotification = new Intent(this, MainActivity.class);
        intentNotification.putExtra("streamingRadio", "streamingRadio");
        PendingIntent pendingIntentOpenApp = PendingIntent.getActivity(this, 0, intentNotification, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPause = new Intent(this, StreamingReceiver.class);
        intentPause.setAction("stop");
        PendingIntent pendingIntentPause = PendingIntent.getBroadcast(this, 12345, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentPlay = new Intent(this, StreamingReceiver.class);
        intentPlay.setAction("start");
        PendingIntent pendingIntentPlay = PendingIntent.getBroadcast(this, 12345, intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent intentExit = new Intent(this, StreamingReceiver.class);
        intentExit.setAction("exit");
        PendingIntent pendingIntentExit = PendingIntent.getBroadcast(this, 12345, intentExit, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.ic_ydig_notif)
                .setTicker("Mendengarkan " + name)
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setContentTitle(name)
                .setContentText("Oleh YDIG")
                .setContentIntent(pendingIntentOpenApp)
                .addAction(android.R.drawable.ic_media_play, "PLAY", pendingIntentPlay)
                .addAction(android.R.drawable.ic_media_pause, "PAUSE", pendingIntentPause)
                .addAction(android.R.drawable.ic_delete,"STOP", pendingIntentExit)
        ;

        startForeground(115, builder.build());

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.e(TAG, "onError: WHAT: " + what + "EXTRA: " + extra);
        Intent intentError = new Intent(this, StreamingReceiver.class);
        intentError.setAction("elor");
        sendBroadcast(intentError);
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {

    }
}
