/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:37 PM
 */

package com.robuxearny.official.activities.impl;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.appodeal.ads.Appodeal;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.activities.impl.games.SpinTheWheelActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.popup.ReviewPopup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MainMenuActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Log.d("Notifications", "Permission granted");
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        SharedPreferences sharedPreferences = getSharedPreferences("review", Context.MODE_PRIVATE);
        int usages = sharedPreferences.getInt("usages", 0);
        usages++;
        sharedPreferences.edit().putInt("review", usages).apply();

        if (usages == 4) {
            ReviewPopup reviewPopup = new ReviewPopup(this);
            reviewPopup.showPopup();
        }
    }

    public void play(View view) {
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
        startActivity(new Intent(this, TicketActivity.class));
    }

    public void redeem(View view) {
        openRedeem();
    }

    public void boosters(View view) {
        startActivity(new Intent(this, BoostersActivity.class));
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void invite(View view) {
        startActivity(new Intent(this, ReferralActivity.class));
    }

    public void earnMore(View view) {
        startActivity(new Intent(this, SurveyActivity.class));
    }

    public void dailyWheel(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Get today's date in yyyy-MM-dd format
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Reference to the user's document in Firestore
            DocumentReference userDocRef = db.collection("users").document(uid);

            // Retrieve the last access time
            userDocRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Get the last access timestamp
                        Timestamp lastAccessTimestamp = document.getTimestamp("lastWheelAccess");
                        String lastAccessDate = "";

                        if (lastAccessTimestamp != null) {
                            // Format the last access date
                            lastAccessDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(lastAccessTimestamp.toDate());
                        }

                        // Check if today's date is different from last access date
                        if (!todayDate.equals(lastAccessDate)) {
                            // Update last access time to now
                            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                                Appodeal.show(this, Appodeal.INTERSTITIAL);
                            }
                            startActivity(new Intent(this, SpinTheWheelActivity.class));


                        } else {
                            // Inform user they can only access once per day
                            Toast.makeText(this, getString(R.string.daily_wheel_once_per_day), Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Log.w("DailyWheel", "Error getting documents.", task.getException());
                }
            });
        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    public void openRedeem() {
        SharedPreferences preferences = getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            Log.d("Coins", "Current UID: " + uid);

            db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d("Coins", "Document Data: " + data);

                        Long coinsLong = document.getLong("coins");
                        if (coinsLong != null) {
                            long coins = coinsLong;
                            preferences.edit().putInt("coins", (int) coins).apply();
                            if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                                Appodeal.show(this, Appodeal.INTERSTITIAL);
                            }
                            startActivity(new Intent(this, RedeemActivity.class));
                        }
                    }
                }
            });
        }
    }
}
