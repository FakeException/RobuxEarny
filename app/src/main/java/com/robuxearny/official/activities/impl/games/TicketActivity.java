/*
 * Created by FakeException on 8/11/23, 2:42 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:39 PM
 */

package com.robuxearny.official.activities.impl.games;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.activities.impl.MainMenuActivity;
import com.robuxearny.official.utils.BoosterUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TicketActivity extends GameActivity {

    private Set<Button> scratchedBlocks;
    private TextView totalPointsTextView;
    private Set<Integer> winningNumbers;
    private int ticketAttempts = 0;

    private List<Button> blockButtons;
    private Activity activity;
    private final int MAX_TICKET_ATTEMPTS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        this.activity = this;

        Appodeal.cache(this, Appodeal.REWARDED_VIDEO);

        MaterialToolbar tbToolBar = findViewById(R.id.ticket_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> {
            Intent menu = new Intent(this, MainMenuActivity.class);
            startActivity(menu);
            finish();
        });

        updateAndDisplayCoins();

        Button confirmButton = findViewById(R.id.confirmButton);

        initializeGame();

        blockButtons = new ArrayList<>();
        blockButtons.add(findViewById(R.id.block1));
        blockButtons.add(findViewById(R.id.block2));
        blockButtons.add(findViewById(R.id.block3));
        blockButtons.add(findViewById(R.id.block4));
        blockButtons.add(findViewById(R.id.block5));
        blockButtons.add(findViewById(R.id.block6));
        blockButtons.add(findViewById(R.id.block7));
        blockButtons.add(findViewById(R.id.block8));
        blockButtons.add(findViewById(R.id.block9));

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        this.scratchedBlocks = new HashSet<>();

        for (Button block : blockButtons) {
            block.setOnClickListener(view -> processBlockClick(block, view));
        }

        confirmButton.setOnClickListener(view -> {
            try {
                confirmTicket();
            } catch (ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        updateWinningNumbersTextView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // We do this to prevent reset of coins before saving them
        if (this.scratchedBlocks.isEmpty()) {
            updateAndDisplayCoins();
        }
    }

    private void updateAndDisplayCoins() {
        int coins = getPreferences().getInt("coins", 0);
        setTotalPoints(coins);

        this.totalPointsTextView = findViewById(R.id.totalPointsTextView);
        totalPointsTextView.setText(getString(R.string.total_points, coins));
    }

    private void initializeGame() {
        this.winningNumbers = generateWinningNumbers();
    }

    private Set<Integer> generateWinningNumbers() {
        Set<Integer> numbers = new HashSet<>();
        Random random = new Random();
        while (numbers.size() < 3) {
            int number = random.nextInt(9) + 1;
            numbers.add(number);
        }
        return numbers;
    }

    private void processBlockClick(Button block, View view) {
        if (scratchedBlocks.contains(block)) {
            return;
        }

        int randomNumber = generateRandomNumber();
        block.setText(String.valueOf(randomNumber));
        block.setTextColor(Color.BLACK);

        if (this.winningNumbers.contains(randomNumber)) {
            int points = generateRandomPoints();
            increasePoints(points);
            playCollectSound();
        }

        bonus(view);

        this.scratchedBlocks.add(block);
        updateTotalPointsTextView(totalPointsTextView);
    }

    private void bonus(View view) {
        int randomNumberBonus = getRandom().nextInt(1000);

        if (randomNumberBonus <= 2) {
            increasePoints(15);
            playCollectSound();
            Snackbar.make(view, R.string.congratulations_you_won_a_20_points_bonus, Snackbar.LENGTH_SHORT).show();
        }
    }

    private int generateRandomNumber() {
        return getRandom().nextInt(9) + 1;
    }

    private int generateRandomPoints() {
        int basePoints = getRandom().nextInt(6) + 2;
        return BoosterUtils.getMoneyBooster(basePoints);
    }

    private void updateWinningNumbersTextView() {
        TextView winningNumbersTextView = findViewById(R.id.winningNumbersTextView);
        StringBuilder numbersBuilder = new StringBuilder();
        for (Integer num : this.winningNumbers) {
            int number = num;
            numbersBuilder.append(number).append(" ");
        }
        String numbers = getString(R.string.winning_numbers, numbersBuilder.toString());
        winningNumbersTextView.setText(numbers);
    }

    private void save() {
        Log.d("Coins", "Document Data: " + getTotalPoints());
        getPrefsEditor().putInt("coins", getTotalPoints()).apply();
        updateCoins(getTotalPoints());
        Toast.makeText(getApplicationContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show();
    }

    private void confirmTicket() throws ExecutionException, InterruptedException {
        if (this.scratchedBlocks.size() == 9) {

            findViewById(R.id.confirmButton).setEnabled(false);

            // Show the confirmation dialog
            MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);

            dialogBuilder
                    .setTitle(R.string.save_coins_title)
                    .setMessage(R.string.save_coins_desc)
                    .setPositiveButton(R.string.save_coins_continue, (dialog, which) -> handleWatchAd())
                    .setNegativeButton(R.string.close, (dialog, which) -> {
                        findViewById(R.id.confirmButton).setEnabled(true);
                        dialog.dismiss();
                    })
                    .setCancelable(false)
                    .show();

        } else {
            Toast.makeText(this, getString(R.string.ticket_finish), Toast.LENGTH_LONG).show();
        }
    }

    private void resetOperations() {
        this.scratchedBlocks.clear();
        this.winningNumbers = generateWinningNumbers();
        updateWinningNumbersTextView();

        ticketAttempts++;

        blockButtons.forEach(button -> {
            button.setText(getString(R.string.x));
            button.setTextColor(Color.WHITE);
        });
    }

    private void handleWatchAd() {

        resetOperations();

        Appodeal.show(this, Appodeal.REWARDED_VIDEO);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {
                // Called when rewarded video is loaded
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
            }

            @Override
            public void onRewardedVideoFailedToLoad() {
                // Called when rewarded video failed to load
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                new Handler().postDelayed(() -> {
                    findViewById(R.id.confirmButton).setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.ad_load_fail), Toast.LENGTH_LONG).show();
                }, 5000); // Delay of 5 seconds
            }

            @Override
            public void onRewardedVideoShown() {
                // Called when rewarded video is shown
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
            }

            @Override
            public void onRewardedVideoShowFailed() {
                // Called when rewarded video show failed
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                new Handler().postDelayed(() -> {
                    findViewById(R.id.confirmButton).setEnabled(true);
                    Toast.makeText(getApplicationContext(), getString(R.string.ad_load_fail), Toast.LENGTH_LONG).show();
                }, 5000); // Delay of 5 seconds
            }

            @Override
            public void onRewardedVideoClicked() {
                // Called when rewarded video is clicked
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
            }

            @Override
            public void onRewardedVideoFinished(double amount, String name) {
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
                save();

                findViewById(R.id.confirmButton).setEnabled(true);

                if (ticketAttempts >= MAX_TICKET_ATTEMPTS || shouldSwitchRandomly(MAX_TICKET_ATTEMPTS)) {
                    startRandomGameActivity(true);
                    ticketAttempts = 0;
                }
            }

            @Override
            public void onRewardedVideoClosed(boolean finished) {
                // Called when rewarded video is closed
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
            }

            @Override
            public void onRewardedVideoExpired() {
                // Called when rewarded video is expired
                Appodeal.cache(activity, Appodeal.REWARDED_VIDEO);
            }
        });
    }
}