/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.appodeal.ads.Appodeal;

public class BaseActivity extends AppCompatActivity {
    private BroadcastReceiver networkReceiver;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        this.preferences = getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);
       // this.networkReceiver = new NetworkChangeReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //registerNetworkReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterNetworkReceiver();
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.networkReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        unregisterReceiver(this.networkReceiver);
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public SharedPreferences.Editor getPrefsEditor() {
        return preferences.edit();
    }
}