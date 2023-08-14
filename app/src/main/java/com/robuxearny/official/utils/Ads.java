/*
 * Created by FakeException on 8/11/23, 1:01 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:01 PM
 */

package com.robuxearny.official.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

public class Ads {

    private static AdManagerInterstitialAd ad;
    private static RewardedAd rewardedAd;

    public static void loadRewardedAd(Activity activity) {
        if (rewardedAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(activity, "ca-app-pub-6202710455352099/9711414733", adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    rewardedAd = null;
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd ad) {
                    rewardedAd = ad;
                }
            });
        }
    }

    public static void showRewardedVideo(Activity activity, OnUserEarnedRewardListener listener) {
        if (rewardedAd == null) {
            return;
        }
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                rewardedAd = null;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                loadRewardedAd(activity);
            }
        });

        rewardedAd.show(activity, listener);
    }

    public static void showRewardedVideoActivity(Activity activity, Class<?> open) {

        if (rewardedAd == null) {
            Intent act = new Intent(activity, open);
            activity.startActivity(act);
        }
        rewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdShowedFullScreenContent() {
            }

            @Override
            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                rewardedAd = null;
            }

            @Override
            public void onAdDismissedFullScreenContent() {
                rewardedAd = null;
                loadRewardedAd(activity);
            }
        });

        rewardedAd.show(activity, rewardItem -> {
            Intent act = new Intent(activity, open);
            activity.startActivity(act);
        });
    }

    public static void loadAd(Activity activity) {
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
        AdManagerInterstitialAd.load(activity, "ca-app-pub-6202710455352099/5121141458", adRequest, new AdManagerInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                ad = interstitialAd;
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        ad = null;
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        Log.d("TAG", "The ad failed to show.");
                        ad = null;
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        Log.d("TAG", "The ad was shown.");
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                ad = null;
            }
        });
    }

    public static void showInterstitial(Activity activity) {
        AdManagerInterstitialAd adManagerInterstitialAd = ad;
        if (adManagerInterstitialAd != null) {
            adManagerInterstitialAd.show(activity);
        } else {
            loadAd(activity);
        }
    }
}
