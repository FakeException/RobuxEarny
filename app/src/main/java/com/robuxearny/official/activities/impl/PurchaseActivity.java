/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/8/23, 12:52 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.robuxearny.official.R;
import com.robuxearny.official.Robux;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.utils.SharedPrefsHelper;

import java.util.HashMap;
import java.util.Map;

public class PurchaseActivity extends BaseActivity {

    private int cost;
    private int coins;
    private int robux;
    private int ads;
    private int referralCount;
    private int surveys;
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
        referralCount = getIntent().getIntExtra("referralCount", 0);
        surveys = getIntent().getIntExtra("surveys", 0);

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
        SharedPrefsHelper prefsHelper = getPrefsHelper();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setTitle(getString(R.string.confirmation));
        builder.setMessage(getString(R.string.confirm_desc));
        builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFunctions functions = FirebaseFunctions.getInstance();

                Map<String, Object> data = getMessageData(user, prefsHelper);

                functions
                        .getHttpsCallable("redeem")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            // Handle success
                            Toast.makeText(this, getString(R.string.request), Toast.LENGTH_LONG).show();

                            prefsHelper.resetAll();

                            int newCoins = coins - cost;
                            updateCoins(user.getUid(), newCoins);
                            getPrefsEditor().putInt("coins", newCoins).apply();

                            Intent notificationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + activity.getPackageName()));
                            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

                            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "robux_earny")
                                    .setSmallIcon(R.drawable.robux5)
                                    .setContentTitle(getString(R.string.redeem_title))
                                    .setContentText(getString(R.string.redeem_desc))
                                    .setContentIntent(pendingIntent)
                                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                            Robux.getInstance().getNotificationManager().notify(1, notification.build());

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

    @NonNull
    private Map<String, Object> getMessageData(FirebaseUser user, SharedPrefsHelper prefsHelper) {
        Map<String, Object> data = new HashMap<>();
        data.put("messageContent",
                "Id: " + user.getUid()
                + "\nRobux: " + robux
                + "\nGamepass: " + gamePass.getText()
                + "\nWatched ads: " + ads
                + "\nUsage time since last redeem: " + prefsHelper.getUsageTimeString()
                + "\nReferral Count: " + referralCount
                + "\nCompleted Surveys: " + surveys
                + "\nMoney earned with Daily Wheel since last redeem: " + prefsHelper.getMoneyEarnedWithDailyWheel()
                + "\nMoney earned with Ticket since last redeem: " + prefsHelper.getMoneyEarnedWithTicket()
                + "\nMoney earned with SM since last redeem: " + prefsHelper.getMoneyEarnedWithSM()
                + "\nMoney earned with CB since last redeem: " + prefsHelper.getMoneyEarnedWithCB()
        );
        return data;
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
