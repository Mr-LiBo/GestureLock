package com.cins.gesturelockdemo.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.cins.gesturelockdemo.R;
import com.cins.gesturelockdemo.util.BitmapUtil;
import com.cins.gesturelockdemo.util.cache.ACache;
import com.cins.gesturelockdemo.util.constant.Constant;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Eric on 2016/10/31.
 */

public class SplashActivity extends AppCompatActivity {

    @Bind(R.id.splash_iv)
    ImageView splashIv;

    private ACache mACache;
    private Bitmap splashBitmap;
    private int screenWidth, screenHeight;
    private Handler handler = new Handler(){};

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        init();
    }

    private void init() {
        mACache = ACache.get(this);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

        splashBitmap = BitmapUtil.resizeBitmap(screenWidth, screenHeight - getStatusBarHeight(),
                BitmapFactory.decodeResource(getResources(), R.mipmap.splash));
        splashIv.setImageBitmap(splashBitmap);
        doJump();
    }

    private void doJump() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String gesturePassword = mACache.getAsString(Constant.GESTURE_PASSWORD);
                if(gesturePassword == null || "".equals(gesturePassword)) {
                    Intent intent = new Intent(SplashActivity.this, CreateGestureActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(SplashActivity.this, GestureLoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}

