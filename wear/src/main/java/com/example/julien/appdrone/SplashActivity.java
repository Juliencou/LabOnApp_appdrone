package com.example.julien.appdrone;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.wearable.activity.WearableActivity;

public class SplashActivity extends WearableActivity {

    private static final int SPLASH_DELAY = 4000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }

    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, DeviceListActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
