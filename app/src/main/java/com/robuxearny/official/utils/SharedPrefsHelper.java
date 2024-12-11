/*
 * Created by FakeException on 2024/12/02 9:51
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/02 9:51
 */

package com.robuxearny.official.utils;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.enums.Gender;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

public class SharedPrefsHelper {
    private static final String PREFS_NAME = "RobuxEarny";
    private static final String KEY_USAGE_TIME = "usage_time";
    private static final String KEY_DAILY_WHEEL_TIME = "usage_daily_wheel";
    private static final String KEY_TICKET_TIME = "usage_ticket";
    private static final String KEY_SM_TIME = "usage_sm";
    private static final String KEY_CB_TIME = "usage_cb";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_AGE = "age";
    private static final String KEY_USER_INFO = "has_user_info";

    private final SharedPreferences sharedPrefs;

    private final SharedPreferences.Editor editor;

    private final FirebaseFirestore firestore;

    public SharedPrefsHelper(Context context) {
        sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = sharedPrefs.edit();

        this.firestore = FirebaseFirestore.getInstance();
    }

    public int getAge() {
        return sharedPrefs.getInt(KEY_AGE, 18);
    }

    public String getGender() {
        return sharedPrefs.getString(KEY_GENDER, Gender.MALE.toString());
    }

    public boolean hasUserInfo() {
        return sharedPrefs.getBoolean(KEY_USER_INFO, false);
    }

    public void setAge(int age) {
        editor.putInt(KEY_AGE, age);
        editor.apply();
    }

    public void setUserInfo(boolean hasUserInfo) {
        editor.putBoolean(KEY_USER_INFO, hasUserInfo);
        editor.apply();
    }

    public void setGender(Gender gender) {
        editor.putString(KEY_GENDER, gender.toString());
        editor.apply();
    }

    public void saveUserInfoToDB() {
        FirebaseUser userId = FirebaseAuth.getInstance().getCurrentUser(); // Get the user's ID
        if (userId != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("gender", getGender());
            userData.put("age", getAge());
            userData.put("hasUserInfo", hasUserInfo());

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

    public long getStartUsageTime() {
        return sharedPrefs.getLong(KEY_USAGE_TIME, 0);
    }

    public void setStartUsageTime(long timeMillis) {
        editor.putLong(KEY_USAGE_TIME, timeMillis).apply();
    }

    public int getMoneyEarnedWithDailyWheel() {
        return sharedPrefs.getInt(KEY_DAILY_WHEEL_TIME, 0);
    }

    public void addDailyWheelEarnings(int money) {
        int totalMoney = getMoneyEarnedWithDailyWheel() + money;
        editor.putInt(KEY_DAILY_WHEEL_TIME, totalMoney);
        editor.apply();
    }

    public int getMoneyEarnedWithTicket() {
        return sharedPrefs.getInt(KEY_TICKET_TIME, 0);
    }

    public void addTicketEarnings(int money) {
        int totalMoney = getMoneyEarnedWithTicket() + money;
        editor.putInt(KEY_TICKET_TIME, totalMoney);
        editor.apply();
    }

    public int getMoneyEarnedWithSM() {
        return sharedPrefs.getInt(KEY_SM_TIME, 0);
    }

    public void addSMEarnings(int money) {
        int totalMoney = getMoneyEarnedWithSM() + money;
        editor.putInt(KEY_SM_TIME, totalMoney);
        editor.apply();
    }

    public int getMoneyEarnedWithCB() {
        return sharedPrefs.getInt(KEY_CB_TIME, 0);
    }

    public void addCBEarnings(int money) {
        int totalMoney = getMoneyEarnedWithCB() + money;
        editor.putInt(KEY_CB_TIME, totalMoney);
        editor.apply();
    }

    public String getUsageTimeString(Context context) {

        if (PermissionsUtils.hasNotUsagePermission(context)) {
            return null;
        }

        long startUsageTime = getStartUsageTime();
        long endUsageTime = System.currentTimeMillis();

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(startUsageTime, endUsageTime);

        long totalTimeUsageInMillis = Objects.requireNonNull(lUsageStatsMap.get("com.robuxearny.official")).
                getTotalTimeInForeground();

        // Format the time using SimpleDateFormat
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        formatter.setTimeZone(TimeZone.getTimeZone("UTC")); // Set timezone to UTC to avoid timezone issues
        return formatter.format(new Date(totalTimeUsageInMillis));
    }

    public void resetAll() {
        editor.putLong(KEY_USAGE_TIME, System.currentTimeMillis()).apply();
        editor.putInt(KEY_DAILY_WHEEL_TIME, 0).apply();
        editor.putInt(KEY_TICKET_TIME, 0).apply();
        editor.putInt(KEY_SM_TIME, 0).apply();
        editor.putInt(KEY_CB_TIME, 0).apply();
    }
}