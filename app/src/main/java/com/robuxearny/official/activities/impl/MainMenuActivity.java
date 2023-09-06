/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:37 PM
 */

package com.robuxearny.official.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.utils.Ads;

public class MainMenuActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Ads.loadRewardedAd(this);

        setupBanners(findViewById(R.id.adView), findViewById(R.id.adView2), findViewById(R.id.adView3));
    }

    public void play(View view) {
        Ads.showRewardedVideoActivity(this, TicketActivity.class);
    }

    public void redeem(View view) {
        Ads.showRewardedVideoActivity(this, RedeemActivity.class);
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void invite(View view) {
        startActivity(new Intent(this, ReferralActivity.class));
    }
}
