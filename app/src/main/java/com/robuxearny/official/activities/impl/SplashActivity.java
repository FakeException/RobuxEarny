/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 12:45 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.work.ArrayCreatingInputMerger;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.ads.MobileAds;
import com.robuxearny.official.R;
import com.robuxearny.official.Robux;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.listeners.ActivityFinishListener;
import com.robuxearny.official.utils.Dialogs;
import com.robuxearny.official.utils.RootChecker;

/**
 * Splash Activity that inflates splash activity xml.
 */
public class SplashActivity extends BaseActivity implements ActivityFinishListener {
    private static final String LOG_TAG = "SplashActivity";

    /**
     * Number of seconds to count down before showing the app open ad. This simulates the time needed
     * to load the app.
     */
    private static final long COUNTER_TIME = 3;

    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeMobileAdsSdk();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RootChecker.class).setInputMerger(ArrayCreatingInputMerger.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        createTimer(COUNTER_TIME);
    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     *
     * @param seconds the number of seconds that the timer counts down from
     */
    private void createTimer(long seconds) {
        final TextView counterTextView = findViewById(R.id.timer);

        CountDownTimer countDownTimer =
                new CountDownTimer(seconds * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = ((millisUntilFinished / 1000) + 1);
                        counterTextView.setText(getString(R.string.load, secondsRemaining));
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        counterTextView.setText(getString(R.string.done));

                        Application application = getApplication();

                        // If the application is not an instance of MyApplication, log an error message and
                        // start the MainActivity without showing the app open ad.
                        if (!(application instanceof Robux)) {
                            Log.e(LOG_TAG, "Failed to cast application to RobuxEarny.");
                            startMainActivity();
                            return;
                        }

                        // Show the app open ad.
                        ((Robux) application)
                                .showAdIfAvailable(
                                        SplashActivity.this,
                                        () -> startMainActivity());
                    }
                };
        countDownTimer.start();
    }

    /**
     * Start the MainActivity.
     */
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }

    @Override
    public void onActivityFinishRequested() {
        Dialogs.showNoRootDialog(this);
    }

    private void initializeMobileAdsSdk() {
        // Log the Mobile Ads SDK version.
        Log.d("Ads", "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        MobileAds.initialize(
                this,
                initializationStatus -> {});
    }

}
