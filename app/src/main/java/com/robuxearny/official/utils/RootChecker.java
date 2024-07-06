/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.utils;

import android.app.Activity;
import android.content.Context;

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
        ActivityFinishListener activityFinishListener;
        RootBeer rootBeer = new RootBeer(getApplicationContext());
        if (rootBeer.isRooted() && (activityFinishListener = this.finishListener) != null) {
            activityFinishListener.onActivityFinishRequested();
        }
        return ListenableWorker.Result.success();
    }
}