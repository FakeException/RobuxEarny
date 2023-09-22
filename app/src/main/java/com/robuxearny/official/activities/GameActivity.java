/*
 * Created by FakeException on 8/11/23, 2:14 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 2:14 PM
 */

package com.robuxearny.official.activities;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.utils.Ads;

import java.util.Random;

public class GameActivity extends BaseActivity {

    private String uid;
    private int totalPoints;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Ads.loadRewardedAd(this);
        Ads.loadAd(this);

        if (currentUser != null) {
            uid = currentUser.getUid();
        }

        mediaPlayer = MediaPlayer.create(this, R.raw.collect);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        random = new Random();
    }

    public void showInterstitial(OnUserEarnedRewardListener listener) {
        Ads.showInterstitial(this, listener);
    }

    public void updateCoins(int newCoins) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("coins", newCoins).addOnSuccessListener(obj -> {
                        Log.d("Coins", "Coins updated");
                    }).addOnFailureListener(exc -> {
                        Log.d("Coins", exc.getMessage());
                    });
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
}
