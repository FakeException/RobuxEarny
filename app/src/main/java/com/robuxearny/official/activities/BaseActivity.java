/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.appodeal.ads.Appodeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.utils.SharedPrefsHelper;

import java.util.HashMap;
import java.util.Map;

public class BaseActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private long startTime;
    private FirebaseFirestore firestore;

    private static final String PREFS_NAME = "RobuxEarny";

    private SharedPrefsHelper prefsHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        this.firestore = FirebaseFirestore.getInstance();
        this.preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.prefsHelper = new SharedPrefsHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTime = System.currentTimeMillis(); // Record start time when activity resumes
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDataToFirebase();
    }

    @Override
    protected void onPause() {
        super.onPause();
        long endTime = System.currentTimeMillis();
        long usageTime = endTime - startTime;
        prefsHelper.addUsageTime(usageTime); // Use the helper method
    }

    private void saveDataToFirebase() {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser(); // Get the user's ID
        if (userId != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("usageTime", prefsHelper.getUsageTimeString());
            userData.put("dailyWheelEarnings", prefsHelper.getMoneyEarnedWithDailyWheel());
            userData.put("ticketEarnings", prefsHelper.getMoneyEarnedWithTicket());
            userData.put("smEarnings", prefsHelper.getMoneyEarnedWithSM());
            userData.put("cbEarnings", prefsHelper.getMoneyEarnedWithCB());

            firestore.collection("users").document(userId.getUid())
                    .update(userData)
                    .addOnSuccessListener(aVoid -> {
                        // Data saved successfully
                        Log.d("Firebase", "Usage data saved successfully");
                    })
                    .addOnFailureListener(e -> {
                        // Handle error
                        Log.e("Firebase", "Error saving usage data, " + e.getMessage());
                    });
        }
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getPrefsEditor() {
        return preferences.edit();
    }

    public SharedPrefsHelper getPrefsHelper() {
        return prefsHelper;
    }
}