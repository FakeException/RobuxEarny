/*
 * Created by FakeException on 2024/11/23 17:24
 * Copyright (c) 2024. All rights reserved.
 * Last modified 2024/11/23 17:24
 */

package com.robuxearny.official.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.appodeal.ads.Appodeal;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.I18NBundle;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.activities.impl.games.SlotMachineActivity;
import com.robuxearny.official.activities.impl.games.TicketActivity;
import com.robuxearny.official.utils.BoosterUtils;

import java.util.Locale;
import java.util.Random;

public class LibGDXBaseGame extends ApplicationAdapter {

    private String uid;
    private I18NBundle localeBundle;

    private BitmapFont font;


    protected void initialize() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            uid = currentUser.getUid();
        }

        BoosterUtils.initialize();

        Locale locale = Locale.getDefault(); //This will get default language of the device
        String encoding = "utf-8";
        if (locale.getLanguage().equals("ja")) {

            Texture texture = new Texture(Gdx.files.internal("newfont2.png"));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            font = new BitmapFont(Gdx.files.internal("newfont2.fnt"), new TextureRegion(texture), false);
        } else {
            Texture texture = new Texture(Gdx.files.internal("arial.png"));
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            font = new BitmapFont(Gdx.files.internal("arial.fnt"), new TextureRegion(texture), false);
        }

        font.getData().setScale(2f); // Double the font size

        localeBundle = I18NBundle.createBundle(Gdx.files.internal("i18n/RE"), locale, encoding);
    }

    public BitmapFont getFont() {
        return font;
    }


    public I18NBundle getLocale() {
        return localeBundle;
    }

    public void updateCoins(int newCoins) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

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
        DocumentReference userRef = db.collection("users").document(uid);

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

    public void startRandomGameActivity(Context context) {
        Appodeal.show((Activity) context, Appodeal.INTERSTITIAL);
        Class<?>[] activities = {SlotMachineActivity.class, TicketActivity.class};
        Random random = new Random();

        // Select a random activity, excluding the current one
        int randomIndex = random.nextInt(activities.length);

        Intent intent = new Intent(context, activities[randomIndex]);
        context.startActivity(intent);
    }

    public String getUid() {
        return uid;
    }
}
