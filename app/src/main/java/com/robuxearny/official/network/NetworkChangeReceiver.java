package com.robuxearny.official.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.robuxearny.official.utils.Dialogs;
import com.robuxearny.official.utils.NetworkUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {
    private final Activity mainActivity;

    public NetworkChangeReceiver(Activity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!NetworkUtils.isInternetConnected(context)) {
            Dialogs.showNoInternetDialog(this.mainActivity);
        }
    }
}