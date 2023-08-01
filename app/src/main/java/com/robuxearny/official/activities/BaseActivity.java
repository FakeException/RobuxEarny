package com.robuxearny.official.activities;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.robuxearny.official.network.NetworkChangeReceiver;

public class BaseActivity extends AppCompatActivity {
    private BroadcastReceiver networkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.networkReceiver = new NetworkChangeReceiver(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerNetworkReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkReceiver();
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.networkReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        unregisterReceiver(this.networkReceiver);
    }
}