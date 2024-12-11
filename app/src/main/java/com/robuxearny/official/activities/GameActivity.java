/*
 * Created by FakeException on 8/11/23, 2:14 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:14 PM
 */

package com.robuxearny.official.activities;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.appodeal.ads.Appodeal;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.games.SlotMachineActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.utils.BoosterUtils;

import java.util.Random;

public class GameActivity extends BaseActivity {

    private int totalPoints;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mediaPlayer = MediaPlayer.create(this, R.raw.collect);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        random = new Random();

        BoosterUtils.initialize();
    }

    public void updateCoins(int newCoins) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("ads", FieldValue.increment(1))
                            .addOnSuccessListener(obj -> Log.d("AdsView", "Ads view update"))
                            .addOnFailureListener(exc -> Log.d("AdsView", "Error: " + exc.getMessage()));

                    userRef.update("coins", newCoins)
                            .addOnSuccessListener(obj -> Log.d("Coins", "Coins updated"))
                            .addOnFailureListener(exc -> Log.d("Coins", "Error: " + exc.getMessage()));

                }
            }

        });
    }

    public void increaseCoins(int inc) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(getUid());

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("coins", FieldValue.increment(inc))
                            .addOnSuccessListener(obj -> Log.d("Coins", "Coins updated"))
                            .addOnFailureListener(exc -> Log.d("Coins", "Error: " + exc.getMessage()));

                }
            }

        });
    }

    public void updateTotalPointsTextView(TextView totalPointsView) {
        totalPointsView.setText(getString(R.string.total_points, this.totalPoints));
    }

    public boolean shouldSwitchRandomly(int maxAttempts) {
        Random random = new Random();
        int randomNumber = random.nextInt(maxAttempts + 1);

        int RANDOM_SWITCH_THRESHOLD = 2;
        return randomNumber < RANDOM_SWITCH_THRESHOLD;
    }

    public void increasePoints(int points) {
        totalPoints += points;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public Vibrator getVibrator() {
        return vibrator;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }

    public Random getRandom() {
        return random;
    }

    public void playCollectSound() {
        getMediaPlayer().start();
        if (getVibrator().hasVibrator()) {
            getVibrator().vibrate(100);
        }
    }

    public void startRandomGameActivity(boolean showAd) {
        if (showAd) {
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
        Class<?>[] activities = {SlotMachineActivity.class, TicketActivity.class, LibGDXActivity.class};
        Random random = new Random();

        int randomIndex = random.nextInt(activities.length);

        Intent intent = new Intent(this, activities[randomIndex]);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
