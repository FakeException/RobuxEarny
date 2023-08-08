/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.utils;

import android.app.Activity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.robuxearny.official.R;

public class Dialogs {
    public static void showNoInternetDialog(final Activity mainActivity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mainActivity);
        builder.setTitle(R.string.no_internet);
        builder.setMessage(R.string.connect_internet);
        builder.setPositiveButton("OK", (dialogInterface, i) -> mainActivity.finishAffinity());
        builder.setCancelable(false);
        builder.show();
    }

    public static void showNoRootDialog(final Activity mainActivity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(mainActivity);
        builder.setTitle(R.string.rooted_device);
        builder.setMessage(R.string.root_desc);
        builder.setPositiveButton("OK", (dialogInterface, i) -> mainActivity.finishAffinity());
        builder.setCancelable(false);
        builder.show();
    }
}