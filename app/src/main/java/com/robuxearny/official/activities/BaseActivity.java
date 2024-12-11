/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appodeal.ads.Appodeal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.utils.SharedPrefsHelper;

public class BaseActivity extends AppCompatActivity {

    private SharedPreferences preferences;

    private static final String PREFS_NAME = "RobuxEarny";

    private SharedPrefsHelper prefsHelper;

    private FirebaseFirestore db;
    private FirebaseUser user;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        this.preferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        this.prefsHelper = new SharedPrefsHelper(this);

        this.db = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        this.uid = user != null ? user.getUid() : null;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
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

    public FirebaseFirestore getDb() {
        return db;
    }

    public FirebaseUser getUser() {
        return user;
    }

    public String getUid() {
        return uid;
    }
}