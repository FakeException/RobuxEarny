/*
 * Created by FakeException Edited by Hisako on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 12/4/24, 4:58 PM
 */

package com.robuxearny.official.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.robuxearny.official.callbacks.ActivityFinishListener;
import com.scottyab.rootbeer.RootBeer;

public class RootChecker extends Worker {
    private ActivityFinishListener finishListener;

    public RootChecker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
        if (context instanceof Activity) {
            this.finishListener = (ActivityFinishListener) context;
        }
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        RootBeer rootBeer = new RootBeer(getApplicationContext());
        
        if (rootBeer.isRooted() || isRootAppInstalled()) {
            if (finishListener != null) {
                finishListener.onActivityFinishRequested();
            }
            
            String packageNameToBlock = "com.gmail.heagoo.appdm"; // HackAppData Blocking 
            if (isAppInstalled(packageNameToBlock)) {
                System.out.println("Blocking application: " + packageNameToBlock);
            }
        }
        
        return ListenableWorker.Result.success();
    }

    private boolean isRootAppInstalled() {
        String[] rootApps = {"com.noshufou.android.su", "com.koushikdutta.superuser", "com.topjohnwu.magisk"}; // Detect Rooting Managing Apps
        for (String packageName : rootApps) {
            if (isAppInstalled(packageName)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false; 
        }
    }
}
