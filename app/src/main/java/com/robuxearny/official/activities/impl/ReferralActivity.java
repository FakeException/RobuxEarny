/*
 * Created by FakeException on 8/14/23, 1:04 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/14/23, 1:04 PM
 */

package com.robuxearny.official.activities.impl;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;

public class ReferralActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);

        setupBanners(findViewById(R.id.adView), findViewById(R.id.adView2), findViewById(R.id.adView3));

        TextView codeView = findViewById(R.id.referralCodeTextView);

        String code = getPreferences().getString("referralCode", "");

        codeView.setText(code);

        Button share = findViewById(R.id.share);
        share.setOnClickListener(click -> shareReferralCode(code));
    }

    private void shareReferralCode(String referralCode) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "RobuxEarny Referral Code");
        intent.putExtra(Intent.EXTRA_TEXT, "This is my RobuxEarny's referral code: " + referralCode + " download the app from Google Play and get a coin bonus!");
        startActivity(Intent.createChooser(intent, "Share Referral Code"));
    }
}
