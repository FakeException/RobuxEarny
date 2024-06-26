/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/8/23, 12:52 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class PurchaseActivity extends BaseActivity {

    private int cost;
    private int coins;
    private int robux;
    private int ads;
    private EditText gamePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchase);
        TextView guide = findViewById(R.id.guide);
        gamePass = findViewById(R.id.gamepass);

        cost = getIntent().getIntExtra("price", 0);
        coins = getIntent().getIntExtra("coins", 0);
        ads = getIntent().getIntExtra("ads", 0);

        robux = Math.round(getIntent().getIntExtra("robux", 0) / 0.70F);

        guide.setText(getString(R.string.guide, robux));
    }

    public void purchase(View view) {
        if (gamePass.getText().toString().isEmpty() || !Patterns.WEB_URL.matcher(gamePass.getText()).matches()) {
            gamePass.setError(getString(R.string.invalid_link));
            return;
        }

        showConfirmation(this);
    }

    private void showConfirmation(final Activity activity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setTitle(getString(R.string.confirmation));
        builder.setMessage(getString(R.string.confirm_desc));
        builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFunctions functions = FirebaseFunctions.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("messageContent", "Id: " + user.getUid() + " Robux: " + robux + " Gamepass: " + gamePass.getText() + " Watched ads: " + ads);

                functions
                        .getHttpsCallable("redeem")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            // Handle success
                            Toast.makeText(this, getString(R.string.request), Toast.LENGTH_LONG).show();

                            int newCoins = coins - cost;
                            updateCoins(user.getUid(), newCoins);
                            getPrefsEditor().putInt("coins", newCoins).apply();

                            Intent menu = new Intent(this, MainMenuActivity.class);
                            startActivity(menu);
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Log.e("Cloud Function", "Error: " + e.getMessage());
                        });
            }
        });
        builder.setNegativeButton(R.string.no, ((dialogInterface, i) -> dialogInterface.cancel()));
        builder.setCancelable(false);
        builder.show();
    }

    private void updateCoins(String uid, int newCoins) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("coins", newCoins).addOnSuccessListener(obj ->
                            Log.d("Coins", "Coins updated")).addOnFailureListener(exc -> Log.d("Coins", exc.getMessage()));

                    userRef.update("ads", 0).addOnSuccessListener(obj ->
                            Log.d("Ads", "Ads updated")).addOnFailureListener(exc -> Log.d("Ads", exc.getMessage()));
                }
            }

        });

    }
}
