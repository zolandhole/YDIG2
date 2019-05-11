package com.surampaksakosoy.ydig;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashScreenActivity extends AppCompatActivity {
    private int mLoading = 0;
    private Handler handler = new Handler();

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
}
