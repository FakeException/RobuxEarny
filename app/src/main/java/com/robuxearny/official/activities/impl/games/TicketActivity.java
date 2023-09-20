/*
 * Created by FakeException on 8/11/23, 2:42 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:39 PM
 */

package com.robuxearny.official.activities.impl.games;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.activities.impl.MainMenuActivity;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class TicketActivity extends GameActivity {

    private Set<Button> scratchedBlocks;
    private TextView totalPointsTextView;
    private Set<Integer> winningNumbers;
    private int ticketAttempts = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket);

        MaterialToolbar tbToolBar = findViewById(R.id.ticket_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> {
            Intent menu = new Intent(this, MainMenuActivity.class);
            startActivity(menu);
            finish();
        });

        int coins = getPreferences().getInt("coins", 0);
        setTotalPoints(coins);

        this.totalPointsTextView = findViewById(R.id.totalPointsTextView);
        totalPointsTextView.setText(getString(R.string.total_points, coins));

        Button confirmButton = findViewById(R.id.confirmButton);

        initializeGame();

        final Button block1 = findViewById(R.id.block1);
        final Button block2 = findViewById(R.id.block2);
        final Button block3 = findViewById(R.id.block3);
        final Button block4 = findViewById(R.id.block4);
        final Button block5 = findViewById(R.id.block5);
        final Button block6 = findViewById(R.id.block6);
        final Button block7 = findViewById(R.id.block7);
        final Button block8 = findViewById(R.id.block8);
        final Button block9 = findViewById(R.id.block9);

        setupBanners(findViewById(R.id.adView), findViewById(R.id.adView2), findViewById(R.id.adView3), findViewById(R.id.adView4));

        this.scratchedBlocks = new HashSet<>();

        block1.setOnClickListener(view -> processBlockClick(block1, view));

        block2.setOnClickListener(view -> processBlockClick(block2, view));

        block3.setOnClickListener(view -> processBlockClick(block3, view));

        block4.setOnClickListener(view -> processBlockClick(block4, view));

        block5.setOnClickListener(view -> processBlockClick(block5, view));

        block6.setOnClickListener(view -> processBlockClick(block6, view));

        block7.setOnClickListener(view -> processBlockClick(block7, view));

        block8.setOnClickListener(view -> processBlockClick(block8, view));

        block9.setOnClickListener(view -> processBlockClick(block9, view));

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

        // Generate bonus random number
        int randomNumberBonus = getRandom().nextInt(100);

        if (randomNumberBonus <= 2) {
            increasePoints(20);
            playCollectSound();
            Snackbar.make(view, R.string.congratulations_you_won_a_20_points_bonus, Snackbar.LENGTH_SHORT).show();
        }

        this.scratchedBlocks.add(block);
        updateTotalPointsTextView(totalPointsTextView);
    }

    private int generateRandomNumber() {
        return getRandom().nextInt(9) + 1;
    }

    private int generateRandomPoints() {
        return getRandom().nextInt(6) + 1;
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

    private void confirmTicket() throws ExecutionException, InterruptedException {
        if (this.scratchedBlocks.size() == 9) {
            this.scratchedBlocks.clear();

            this.winningNumbers = generateWinningNumbers();
            updateWinningNumbersTextView();

            ticketAttempts++;

            showInterstitial(rewardItem -> {
                updateCoins(getTotalPoints());
                getPrefsEditor().putInt("coins", getTotalPoints()).apply();
            });

            for (int i = 1; i <= 9; i++) {
                Button block = findViewById(getResources().getIdentifier("block" + i, "id", getPackageName()));
                block.setText(getString(R.string.x));
                block.setTextColor(Color.WHITE);
            }

            // Check if the maximum attempts have been reached or if it's time to switch randomly
            int MAX_TICKET_ATTEMPTS = 7;
            if (ticketAttempts >= MAX_TICKET_ATTEMPTS || shouldSwitchRandomly(MAX_TICKET_ATTEMPTS)) {
                showInterstitial(rewardItem -> {
                    updateCoins(getTotalPoints());
                    getPrefsEditor().putInt("coins", getTotalPoints()).apply();

                    Intent slot = new Intent(this, SlotMachineActivity.class);
                    startActivity(slot);
                });
            }

        } else {
            Toast.makeText(this, getString(R.string.ticket_finish), Toast.LENGTH_LONG).show();
        }
    }
}