/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 1:37 PM
 */

package com.robuxearny.official.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.utils.Ads;

import java.util.Map;

public class MainMenuActivity extends BaseActivity {

    private int coins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getCurrentUserCoins();

        Ads.loadRewardedAd(this);

        setupBanners(findViewById(R.id.adView), findViewById(R.id.adView2), findViewById(R.id.adView3));
    }

    public void getCurrentUserCoins() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String uid = user.getUid();

            Log.d("Coins", "Current UID: " + uid);

            db.collection("users").document(uid).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data = document.getData();
                        Log.d("Coins", "Document Data: " + data);

                        Long coinsLong = document.getLong("coins");
                        if (coinsLong != null) {
                            long coins = coinsLong;
                            this.coins = (int) coins;
                        } else {
                            this.coins = 0;
                        }
                    } else {
                        this.coins = 0;
                    }
                } else {
                    this.coins = 0;
                }
            });
        }

    }

    public void play(View view) {
        Ads.showRewardedVideoActivity(this, TicketActivity.class, coins);
    }

    public void redeem(View view) {
        Ads.showRewardedVideoActivity(this, RedeemActivity.class, coins);
    }

    public void openSettings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void invite(View view) {
        startActivity(new Intent(this, ReferralActivity.class));
    }
}
