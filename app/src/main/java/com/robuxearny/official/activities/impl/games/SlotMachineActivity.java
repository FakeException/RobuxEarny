/*
 * Created by FakeException on 8/11/23, 2:42 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:29 PM
 */

package com.robuxearny.official.activities.impl.games;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.games.SlotMachine;

import java.util.Random;
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

        setupBanners(findViewById(R.id.adView), findViewById(R.id.adView2), findViewById(R.id.adView3), findViewById(R.id.adView4));

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
        timer.scheduleAtFixedRate(new TimerTask() {
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

        // Check if the maximum attempts have been reached or if it's time to switch randomly
        int MAX_SLOT_ATTEMPTS = 5;
        if (slotAttempts >= MAX_SLOT_ATTEMPTS || shouldSwitchRandomly(MAX_SLOT_ATTEMPTS)) {
            Intent slot = new Intent(this, TicketActivity.class);
            startActivity(slot);
            finish();
        }

        if (slotMachine.checkWin(this)) {

            increasePoints(generateRandomPoints());

            updateTotalPointsTextView(totalPointsTextView);

            showInterstitial(rewardItem -> {
                updateCoins(getTotalPoints());
                getPrefsEditor().putInt("coins", getTotalPoints()).apply();
            });

            getMediaPlayer().start();
            if (getVibrator().hasVibrator()) {
                getVibrator().vibrate(100);
            }
            Toast.makeText(this, R.string.you_win, Toast.LENGTH_SHORT).show();
        }

        spinButton.setEnabled(true);
    }

    private int generateRandomPoints() {
        Random random = new Random();
        return random.nextInt(20) + 1;
    }
}
