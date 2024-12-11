/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:37 PM
 */

package com.robuxearny.official.activities.impl;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import com.appodeal.ads.Appodeal;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.activities.impl.games.MemoryGameActivity;
import com.robuxearny.official.activities.impl.games.SpinTheWheelActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.popup.ReviewPopup;
import com.robuxearny.official.utils.PermissionsUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainMenuActivity extends BaseActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        if (getPrefsHelper().getStartUsageTime() == 0) {
            getPrefsHelper().setStartUsageTime(System.currentTimeMillis());
        }

        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
            if (result) {
                Log.d("Notifications", "Permission granted");
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!PermissionsUtils.hasNotificationsPermission(this)) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (PermissionsUtils.hasNotUsagePermission(this)) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            View parentView = bottomSheetDialog.findViewById(android.R.id.content);

            // Inflate the layout with the parent view
            View bottomSheetView = LayoutInflater.from(this)
                    .inflate(R.layout.usage_permission, (ViewGroup) parentView, false);

            bottomSheetDialog.setContentView(bottomSheetView);

            Button allowButton = bottomSheetView.findViewById(R.id.button_allow_usage_access);
            allowButton.setOnClickListener(v -> {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        }

        TextView coinsText = findViewById(R.id.coins_text);

        int coinsFromStorage = getPreferences().getInt("coins", 0);

        String text = getString(R.string.earned_coins) + ": <big><b>" + coinsFromStorage + "</b></big>";
        coinsText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY));

        int usages = getPreferences().getInt("usages", 0);
        usages++;
        getPrefsEditor().putInt("usages", usages).apply();

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
        startActivity(new Intent(this, MemoryGameActivity.class));
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void invite(View view) {
        startActivity(new Intent(this, ReferralActivity.class));
    }

    public void earnMore(View view) {
        if (getPrefsHelper().hasUserInfo()) {
            startActivity(new Intent(this, OfferwallsActivity.class));
        } else {
            startActivity(new Intent(this, UserInfoActivity.class));
        }
    }

    public void dailyWheel(View view) {
        if (getUid() != null) {

            // Get today's date in yyyy-MM-dd format
            String todayDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            // Reference to the user's document in Firestore
            DocumentReference userDocRef = getDb().collection("users").document(getUid());

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
        if (Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
        startActivity(new Intent(this, RedeemActivity.class));
    }
}
