/*
 * Created by FakeException on 2024/11/20 17:11
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/20 17:11
 */

package com.robuxearny.official.activities.impl.games;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.adefruandta.spinningwheel.SpinningWheelView;
import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.GameActivity;
import com.robuxearny.official.activities.impl.MainMenuActivity;
import com.robuxearny.official.callbacks.SectorsCallback;
import com.robuxearny.official.models.Sector;
import com.robuxearny.official.utils.BackendUtils;

import java.util.ArrayList;
import java.util.List;

public class SpinTheWheelActivity extends GameActivity {

    private SpinningWheelView wheelView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spin_the_wheel);

        MaterialToolbar tbToolBar = findViewById(R.id.spinTheWheel_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);

        Button spinButton = findViewById(R.id.spinButton);
        wheelView = findViewById(R.id.wheelView);

        wheelView.setEnabled(false);

        setupWheel();

        spinButton.setOnClickListener(v -> {
            spinButton.setEnabled(false);
            wheelView.rotate(50, 5000, 10);
        });
    }

    private void setupWheel() {
        BackendUtils backendUtils = new BackendUtils(this);
        backendUtils.fetchSectors(new SectorsCallback() {
            @Override
            public void onSectorsRetrieved(List<Sector> sectors) {
                ArrayList<String> items = new ArrayList<>();

                for (Sector sector : sectors) {
                    if (sector.getTryAgain() != null && sector.getCoins() == null) {
                        items.add(getString(R.string.try_again));
                    } else {
                        items.add(sector.getCoins().toString() + " " + getString(R.string.coins));
                    }
                }

                wheelView.setItems(items);

                wheelView.setOnRotationListener(new SpinningWheelView.OnRotationListener<Object>() {
                    @Override
                    public void onRotation() {
                        //
                    }

                    @Override
                    public void onStopRotation(Object item) {

                        // Reference to the user's document in Firestore
                        DocumentReference userDocRef = getDb().collection("users").document(getUid());

                        userDocRef.update("lastWheelAccess", FieldValue.serverTimestamp())
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("DailyWheel", "Last access time updated successfully.");
                                    // Proceed to open the daily wheel
                                    if (item.equals(getString(R.string.try_again))) {
                                        Toast.makeText(SpinTheWheelActivity.this, getString(R.string.try_again_message), Toast.LENGTH_LONG).show();
                                    } else {
                                        playCollectSound();
                                        int coins = Integer.parseInt(String.valueOf(item).replaceAll("[^0-9]", ""));
                                        getPrefsHelper().addDailyWheelEarnings(coins);
                                        increaseCoins(coins);
                                        Toast.makeText(SpinTheWheelActivity.this, getString(R.string.spin_win, item), Toast.LENGTH_LONG).show();
                                    }
                                    Intent intent = new Intent(SpinTheWheelActivity.this, MainMenuActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("DailyWheel", "Error updating last access time", e);
                                });
                    }
                });
            }

            @Override
            public void onError(String errorMessage) {
                Log.e("DailyWheel", errorMessage);
            }
        });
    }
}
