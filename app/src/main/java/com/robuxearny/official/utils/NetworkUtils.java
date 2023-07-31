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