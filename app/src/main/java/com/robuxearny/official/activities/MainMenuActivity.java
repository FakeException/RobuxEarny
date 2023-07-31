package com.robuxearny.official.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.button.MaterialButton;
import com.robuxearny.official.R;

public class MainMenuActivity extends BaseActivity {

    private RewardedAd rewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        loadRewardedAd();

        AdView adView = findViewById(R.id.adView);
        AdView adView2 = findViewById(R.id.adView2);
        AdView adView3 = findViewById(R.id.adView3);

        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2.loadAd(adRequest);
        adView3.loadAd(adRequest);


        MaterialButton buttonPlay = findViewById(R.id.buttonPlay);
        MaterialButton buttonRedeem = findViewById(R.id.buttonRedeem);

        buttonPlay.setOnClickListener(v -> {
            showRewardedVideo();
            startActivity(new Intent(this, TicketActivity.class));
        });

        buttonRedeem.setOnClickListener(v -> {
            // Handle "Redeem" button click here
            // Start the appropriate activity for redeeming rewards
        });
    }

    public void loadRewardedAd() {
        if (this.rewardedAd == null) {
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, "ca-app-pub-6202710455352099/9711414733", adRequest, new RewardedAdLoadCallback() {
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

    private void showRewardedVideo() {
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
                loadRewardedAd();
            }
        });

        this.rewardedAd.show(this, rewardItem -> {

        });
    }
}
