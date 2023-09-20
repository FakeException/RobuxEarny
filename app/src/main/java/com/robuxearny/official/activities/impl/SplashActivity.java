/*
 * Created by FakeException on 9/20/23, 10:44 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 9/20/23, 10:44 AM
 */
package com.robuxearny.official.activities.impl;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;
import com.robuxearny.official.R;
import com.robuxearny.official.Robux;
import com.robuxearny.official.utils.GoogleMobileAdsConsentManager;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Splash Activity that inflates splash activity xml.
 */
public class SplashActivity extends AppCompatActivity {
    private static final String LOG_TAG = "SplashActivity";
    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    /**
     * Number of milliseconds to count down before showing the app open ad. This simulates the time
     * needed to load the app.
     */
    private static final long COUNTER_TIME_MILLISECONDS = 5000;

    private long secondsRemaining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
        createTimer();

        GoogleMobileAdsConsentManager.getInstance(this)
                .gatherConsent(
                        this,
                        consentError -> {
                            if (consentError != null) {
                                // Consent not obtained in current session.
                                Log.w(
                                        LOG_TAG,
                                        String.format(
                                                "%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                            }

                            if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds()) {
                                initializeMobileAdsSdk();
                            }

                            if (secondsRemaining <= 0) {
                                startMainActivity();
                            }
                        });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds()) {
            initializeMobileAdsSdk();
        }
    }

    /**
     * Create the countdown timer, which counts down to zero and show the app open ad.
     */
    private void createTimer() {
        final TextView counterTextView = findViewById(R.id.timer);

        CountDownTimer countDownTimer =
                new CountDownTimer(SplashActivity.COUNTER_TIME_MILLISECONDS, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
                        counterTextView.setText(getString(R.string.load, secondsRemaining));
                    }

                    @Override
                    public void onFinish() {
                        secondsRemaining = 0;
                        counterTextView.setText(R.string.done);

                        Application application = getApplication();
                        ((Robux) application)
                                .showAdIfAvailable(
                                        SplashActivity.this,
                                        () -> {
                                            // Check if the consent form is currently on screen before moving to the
                                            // main
                                            // activity.
                                            if (GoogleMobileAdsConsentManager.getInstance(SplashActivity.this)
                                                    .canRequestAds()) {
                                                startMainActivity();
                                            }
                                        });
                    }
                };
        countDownTimer.start();
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);

        // Load an ad.
        Application application = getApplication();
        ((Robux) application).loadAd(this);
    }

    /**
     * Start the MainActivity.
     */
    public void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        this.startActivity(intent);
    }
}