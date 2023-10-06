/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 12:45 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.splashscreen.SplashScreen;
import androidx.viewpager.widget.ViewPager;
import androidx.work.ArrayCreatingInputMerger;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.appupdate.AppUpdateOptions;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.Robux;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.IntroSliderAdapter;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.listeners.ActivityFinishListener;
import com.robuxearny.official.utils.Dialogs;
import com.robuxearny.official.utils.GoogleMobileAdsConsentManager;
import com.robuxearny.official.utils.ReferralCodeGenerator;
import com.robuxearny.official.utils.RootChecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements ActivityFinishListener {

    private LinearLayout indicatorLayout;
    private SignInClient oneTapClient;
    private FirebaseAuth mAuth;
    private IntroSliderAdapter introSliderAdapter;
    private ActivityResultLauncher<IntentSenderRequest> updateLauncher;
    private AppUpdateManager appUpdateManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(RootChecker.class).setInputMerger(ArrayCreatingInputMerger.class).build();
        WorkManager.getInstance(this).enqueue(workRequest);

        oneTapClient = Identity.getSignInClient(this);
        mAuth = FirebaseAuth.getInstance();

        ActivityResultLauncher<IntentSenderRequest> oneTapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null);

                                mAuth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(this, task -> {
                                            if (task.isSuccessful()) {
                                                if (mAuth.getCurrentUser() != null) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show();

                                                    if (!introSliderAdapter.getRefCode().isEmpty()) {
                                                        saveData(user.getUid(), 100);
                                                    } else {
                                                        saveData(user.getUid(), 0);
                                                    }

                                                    Intent intent = new Intent(this, MainMenuActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            } else {
                                                Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        GoogleMobileAdsConsentManager.getInstance(this)
                .gatherConsent(
                        this,
                        consentError -> {
                            if (consentError != null) {
                                // Consent not obtained in current session.
                                Log.w(
                                        "Ad consent",
                                        String.format(
                                                "%s: %s", consentError.getErrorCode(), consentError.getMessage()));
                            }

                            if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds()) {
                                initializeMobileAdsSdk();
                            }
                        });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (GoogleMobileAdsConsentManager.getInstance(this).canRequestAds()) {
            initializeMobileAdsSdk();
        }

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {

            ((Robux) getApplication())
                    .showAdIfAvailable(
                            this,
                            () -> {
                                Intent intent = new Intent(this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            });

        } else {
            ViewPager viewPager = findViewById(R.id.viewPager);
            indicatorLayout = findViewById(R.id.indicatorLayout);

            List<IntroSlide> introSlides = new ArrayList<>();
            introSlides.add(new IntroSlide(getString(R.string.welcome), getString(R.string.welcome_desc)));
            introSlides.add(new IntroSlide(getString(R.string.coinsystem), getString(R.string.coinsystem_desc)));
            introSlides.add(new IntroSlide(getString(R.string.ready), getString(R.string.ready_desc)));

            introSliderAdapter = new IntroSliderAdapter(this, introSlides, oneTapLauncher, oneTapClient);
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
    }

    private void saveData(String uid, int coinAmount) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(uid);

        SharedPreferences.Editor editor = getPrefsEditor();

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    String referral = ReferralCodeGenerator.generateReferralCode();
                    userMap.put("uid", uid);
                    userMap.put("coins", coinAmount);
                    userMap.put("referral", referral);
                    userMap.put("ads", 0);

                    editor.putString("referralCode", referral);
                    editor.apply();

                    userDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User data saved successfully.");
                                saveReferral(uid, referral);

                                if (!introSliderAdapter.getRefCode().isEmpty()) {
                                    if (!introSliderAdapter.getReferrer().equals(uid)) {
                                        updateCoins(introSliderAdapter.getReferrer(), 200);
                                    }
                                }

                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data: " + e.getMessage()));
                } else {

                    editor.putString("referralCode", document.getString("referral"));

                    Long coinsLong = document.getLong("coins");
                    if (coinsLong != null) {
                        long coins = coinsLong;
                        editor.putInt("coins", (int) coins);
                    }

                    editor.apply();
                }
            }
        });
    }

    private void saveReferral(String uid, String code) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference codeDocRef = db.collection("referralCodes").document(code);

        codeDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);

                    codeDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data saved successfully."))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data: " + e.getMessage()));
                }
            }
        });
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

    public void updateCoins(String uid, int newCoins) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("coins", FieldValue.increment(newCoins))
                            .addOnSuccessListener(obj -> Log.d("Coins", "Coins updated"))
                            .addOnFailureListener(exc -> Log.d("Coins", exc.getMessage()));
                }
            }

        });

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

    private void initializeMobileAdsSdk() {

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this);
    }
}
