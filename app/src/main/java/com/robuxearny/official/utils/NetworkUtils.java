/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public static boolean isInternetConnected(Context context) {
        NetworkInfo activeNetwork;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager != null && (activeNetwork = connectivityManager.getActiveNetworkInfo()) != null && activeNetwork.isConnectedOrConnecting();
    }
}