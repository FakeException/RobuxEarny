/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/11/23, 12:45 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.IntroSliderAdapter;
import com.robuxearny.official.data.IntroSlide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    private LinearLayout indicatorLayout;
    private SignInClient oneTapClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        oneTapClient = Identity.getSignInClient(this);
        mAuth = FirebaseAuth.getInstance();

        ActivityResultLauncher<IntentSenderRequest> oneTapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartIntentSenderForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        try {
                            SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                            String idToken = credential.getGoogleIdToken();
                            if (idToken != null) {
                                AuthCredential authCredential = GoogleAuthProvider.getCredential(credential.getGoogleIdToken(), null);

                                mAuth.signInWithCredential(authCredential)
                                        .addOnCompleteListener(this, task -> {
                                            if (task.isSuccessful()) {
                                                if (mAuth.getCurrentUser() != null) {
                                                    FirebaseUser user = mAuth.getCurrentUser();
                                                    Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show();
                                                    saveData(user.getUid());

                                                    Intent intent = new Intent(this, MainMenuActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }

                                            } else {
                                                Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } catch (ApiException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
            finish();
        } else {
            ViewPager viewPager = findViewById(R.id.viewPager);
            indicatorLayout = findViewById(R.id.indicatorLayout);

            List<IntroSlide> introSlides = new ArrayList<>();
            introSlides.add(new IntroSlide(getString(R.string.welcome), getString(R.string.welcome_desc)));
            introSlides.add(new IntroSlide(getString(R.string.coinsystem), getString(R.string.coinsystem_desc)));
            introSlides.add(new IntroSlide(getString(R.string.ready), getString(R.string.ready_desc)));

            IntroSliderAdapter introSliderAdapter = new IntroSliderAdapter(this, introSlides, oneTapLauncher, oneTapClient);
            viewPager.setAdapter(introSliderAdapter);

            setupIndicator(introSlides.size());

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    setIndicator(position);
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        }
    }

    private void saveData(String uid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(uid);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("coins", 0);

                    userDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User data saved successfully.");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error saving user data: " + e.getMessage());
                            });
                }
            }
        });
    }

    private void setupIndicator(int count) {
        ImageView[] indicators = new ImageView[count];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageResource(R.drawable.indicator_inactive);
            indicators[i].setLayoutParams(layoutParams);
            indicatorLayout.addView(indicators[i]);
        }

        setIndicator(0);
    }

    private void setIndicator(int position) {
        int childCount = indicatorLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView indicator = (ImageView) indicatorLayout.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.indicator_active);
            } else {
                indicator.setImageResource(R.drawable.indicator_inactive);
            }
        }
    }
}
