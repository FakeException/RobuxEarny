/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 12:45 PM
 */

package com.robuxearny.official.activities.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.ViewPager;
import androidx.work.ArrayCreatingInputMerger;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.appodeal.ads.Appodeal;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.IntroSliderAdapter;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.listeners.ActivityFinishListener;
import com.robuxearny.official.utils.Dialogs;
import com.robuxearny.official.utils.RootChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements ActivityFinishListener {

    private LinearLayout indicatorLayout;

    private IntroSliderAdapter introSliderAdapter;
    private ActivityResultLauncher<IntentSenderRequest> updateLauncher;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        setupUpdateManager();
        rootChecker();

        Appodeal.initialize(this, "697e9088ec11bcc717870003a0bf6510f5d203f744b36e9b", Appodeal.BANNER | Appodeal.INTERSTITIAL | Appodeal.REWARDED_VIDEO, errors -> {

            ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            Appodeal.show(this, Appodeal.BANNER_VIEW);

            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

            if (currentUser != null) {

                retrieveMoney();

                Intent intent = new Intent(this, MainMenuActivity.class);
                startActivity(intent);
                finish();

            } else {
                ViewPager viewPager = findViewById(R.id.viewPager);
                indicatorLayout = findViewById(R.id.indicatorLayout);

                List<IntroSlide> introSlides = new ArrayList<>();
                introSlides.add(new IntroSlide(getString(R.string.welcome), getString(R.string.welcome_desc)));
                introSlides.add(new IntroSlide(getString(R.string.coinsystem), getString(R.string.coinsystem_desc)));
                introSlides.add(new IntroSlide(getString(R.string.ready), getString(R.string.ready_desc)));

                introSliderAdapter = new IntroSliderAdapter(this, introSlides);
                viewPager.setAdapter(introSliderAdapter);

                setupIndicator(introSlides.size());

                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        setIndicator(position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }
                });
            }
        });
    }

    public void retrieveMoney() {
        SharedPreferences preferences = getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            Log.d("Coins", "Current UID: " + uid);

            db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("Coins", "Document Data: " + document);

                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d("Coins", "Document Data: " + data);

                        Long coinsLong = document.getLong("coins");
                        if (coinsLong != null) {
                            long coins = coinsLong;
                            preferences.edit().putInt("coins", (int) coins).apply();
                        }
                    }
                }
            });
        }
    }

    private void setupUpdateManager() {

        appUpdateManager = AppUpdateManagerFactory.create(this);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        updateLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    // handle callback
                    if (result.getResultCode() != RESULT_OK) {
                        Log.d("Update", "Update flow failed! Result code: " + result.getResultCode());
                    }
                });

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        updateLauncher,
                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE)
                                .setAllowAssetPackDeletion(true)
                                .build());
            }
        });
    }

    private void rootChecker() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RootChecker.class).setInputMerger(ArrayCreatingInputMerger.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);
    }



    private void setupIndicator(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }

        setIndicator(0);
    }

    private void setIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.indicator_active);
            } else {
                indicator.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }

    @Override
    public void onActivityFinishRequested() {
        Dialogs.showNoRootDialog(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability()
                                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                appUpdateManager.startUpdateFlowForResult(
                                        appUpdateInfo,
                                        updateLauncher,
                                        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build());
                            }
                        });
    }
}
