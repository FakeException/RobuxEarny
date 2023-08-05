/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.utils;

import android.app.Activity;

import androidx.appcompat.app.AlertDialog;

public class Dialogs {
    public static void showNoInternetDialog(final Activity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please connect to the internet to use this app.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> mainActivity.finishAffinity());
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void showNoRootDialog(final Activity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Rooted Device");
        builder.setMessage("You are not allowed to use this app on rooted devices.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> mainActivity.finishAffinity());
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}