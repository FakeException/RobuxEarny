/*
 * Created by FakeException on 8/14/23, 1:04 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/14/23, 1:04 PM
 */

package com.robuxearny.official.activities.impl;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.utils.ReferralUtils;

public class ReferralActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        MaterialToolbar tbToolBar = findViewById(R.id.referral_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        TextView codeView = findViewById(R.id.referralCodeTextView);

        String code = getPreferences().getString("myReferralCode", "");

        if (code.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getUid();

            SharedPreferences preferences = getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = preferences.edit();

            ReferralUtils.saveUserReferral(db, uid, (referralCode) -> {
                if (referralCode != null) {
                    // Store in SharedPreferences
                    editor.putString("myReferralCode", referralCode).apply();
                    codeView.setText(referralCode);
                    Log.d("Referral", "Your referral code is: " + referralCode);
                } else {
                    // Handle the case where no referral code was found
                    Log.w("Referral", "No referral code found for the user.");
                }
            });
        }

        codeView.setText(code);

        Button share = findViewById(R.id.share);
        share.setOnClickListener(click -> shareReferralCode(code));
    }

    private void shareReferralCode(String referralCode) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.robuxearny_referral_code));
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.robuxearny_referral_desc, referralCode));
        startActivity(Intent.createChooser(intent, getString(R.string.share_referral_code)));
    }
}
