/*
 * Created by FakeException on 8/11/23, 2:42 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:29 PM
 */

package com.robuxearny.official.activities.impl.games;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.games.SlotMachine;
import com.robuxearny.official.utils.BoosterUtils;

import java.util.Timer;
import java.util.TimerTask;

public class SlotMachineActivity extends GameActivity {

    private SlotMachine slotMachine;
    private ImageView[] reelViews;
    private Button spinButton;
    private final int numReels = 3;
    private int slotAttempts = 0;
    private TextView totalPointsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slotmachine);

        totalPointsTextView = findViewById(R.id.totalPointsTextView);

        int points = getPreferences().getInt("coins", 0);
        setTotalPoints(points);
        totalPointsTextView.setText(getString(R.string.total_points, points));

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        // Initialize the slot machine and views
        slotMachine = new SlotMachine(this);
        reelViews = new ImageView[numReels];
        reelViews[0] = findViewById(R.id.reel1);
        reelViews[1] = findViewById(R.id.reel2);
        reelViews[2] = findViewById(R.id.reel3);
        spinButton = findViewById(R.id.spinButton);

        // Set the initial symbols on the reels
        updateReelViews();

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);
            spinReels();
        });
    }

    private void updateReelViews() {
        Drawable[] currentReels = slotMachine.getCurrentReels();
        for (int i = 0; i < numReels; i++) {
            reelViews[i].setImageDrawable(currentReels[i]);
        }
    }

    private void spinReels() {
        final int spins = 3;
        final long delayBetweenSpins = 500;

        // Start spinning the reels
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            private int spinCount = 0;

            @Override
            public void run() {
                runOnUiThread(() -> {
                    slotMachine.spin(SlotMachineActivity.this);
                    updateReelViews();
                    spinCount++;

                    if (spinCount >= spins) {
                        timer.cancel();
                        handleGameResult();
                    }
                });
            }
        }, 0, delayBetweenSpins);
    }

    private void handleGameResult() {

        slotAttempts++;

        if (slotMachine.checkWin(this)) {

            Appodeal.cache(this, Appodeal.REWARDED_VIDEO);

            increasePoints(generateRandomPoints());

            updateTotalPointsTextView(totalPointsTextView);

            save();
        }

        spinButton.setEnabled(true);

        int MAX_SLOT_ATTEMPTS = 5;
        if (slotAttempts >= MAX_SLOT_ATTEMPTS || shouldSwitchRandomly(MAX_SLOT_ATTEMPTS)) {
            startRandomGameActivity(true);
        }
    }

    private void save() {
        getPrefsEditor().putInt("coins", getTotalPoints()).apply();
        updateCoins(getTotalPoints());

        playCollectSound();

        Toast.makeText(getApplicationContext(), R.string.you_win, Toast.LENGTH_SHORT).show();
    }

    private int generateRandomPoints() {
        int basePoints = getRandom().nextInt(21) + 8;
        return BoosterUtils.getMoneyBooster(basePoints);
    }
}
