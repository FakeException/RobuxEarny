/*
 * Created by FakeException on 2024/12/07 11:20
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/12/07 11:20
 */

package com.robuxearny.official.survey;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.appsamurai.appsprize.AppReward;
import com.appsamurai.appsprize.AppsPrize;
import com.appsamurai.appsprize.AppsPrizeListener;
import com.appsamurai.appsprize.AppsPrizeNotification;
import com.appsamurai.appsprize.config.AppsPrizeConfig;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.robuxearny.official.R;
import com.robuxearny.official.utils.SharedPrefsHelper;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsPrizeSurvey {


    public AppsPrizeSurvey(Context context, String uid) throws IOException, GooglePlayServicesRepairableException, GooglePlayServicesNotAvailableException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {

            String advertId = getAdvertisingIdInBackground(context);

            handler.post(() -> {

                Locale locale = Locale.getDefault();
                String country = locale.getCountry();
                String language = locale.getLanguage();

                SharedPrefsHelper prefsHelper = new SharedPrefsHelper(context);

                AppsPrizeConfig config = new AppsPrizeConfig.Builder()
                        .setCountry(country)
                        .setLanguage(language)
                        .setGender(prefsHelper.getGender())
                        .setAge(prefsHelper.getAge())
                        .build(context.getString(R.string.appsprize_token), advertId, uid);

                AppsPrize.initialize(context, config, new AppsPrizeListener() {
                    @Override
                    public void onInitialize() {
                        Log.d("[AppsPrize]", "MainApplication:onCreate AppsPrize:onInitialize");
                    }

                    @Override
                    public void onInitializeFailed(@NonNull String errorMessage) {
                        Log.d("[AppsPrize]", "MainApplication:onCreate AppsPrize:onInitializeFailed: err: " + errorMessage);
                    }

                    @Override
                    public void onRewardUpdate(@NonNull List<AppReward> rewards) {
                        Log.d("[AppsPrize]", "MainApplication:onCreate AppsPrize:onRewardUpdate: " + rewards);
                    }

                    @Override
                    public void onNotification(@NonNull List<AppsPrizeNotification> notifications) {
                        Log.d("[AppsPrize]", "MainApplication:onCreate AppsPrize:onNotification: " + notifications);
                    }
                });
            });
        });
    }

    private String getAdvertisingIdInBackground(Context context) {
        try {
            AdvertisingIdClient.Info advertisingIdInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
            return advertisingIdInfo.getId();
        } catch (IOException | GooglePlayServicesNotAvailableException | GooglePlayServicesRepairableException e) {
            Log.e("AppsPrizeSurvey", "Error getting advertising ID: " + e.getMessage());
            return null;
        }
    }
}
