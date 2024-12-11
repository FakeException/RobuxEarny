/*
 * Created by FakeException on 2024/12/09 11:14
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/09 11:14
 */

package com.robuxearny.official.utils;

import static android.app.AppOpsManager.MODE_ALLOWED;
import static android.app.AppOpsManager.OPSTR_GET_USAGE_STATS;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

public class PermissionsUtils {

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static boolean hasNotificationsPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean hasNotUsagePermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), context.getPackageName());
        if (mode == AppOpsManager.MODE_DEFAULT) {
            return (context.checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS) != PackageManager.PERMISSION_GRANTED);
        } else {
            return (mode != MODE_ALLOWED);
        }
    }
}
