/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.utils.CodeExistenceCallback;

import java.util.List;

public class IntroSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<IntroSlide> introSlides;
    private final ActivityResultLauncher<IntentSenderRequest> oneTapLauncher;
    private final SignInClient oneTapClient;
    private String refCode;
    private String referrer;

    public IntroSliderAdapter(Context context, List<IntroSlide> introSlides, ActivityResultLauncher<IntentSenderRequest> oneTapLauncher, SignInClient oneTapClient) {
        this.context = context;
        this.introSlides = introSlides;
        this.oneTapLauncher = oneTapLauncher;
        this.oneTapClient = oneTapClient;
        this.refCode = "";
        this.referrer = "";
    }

    private BeginSignInRequest signUpRequest;

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.intro_slide, container, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);
        AdView adView = view.findViewById(R.id.adView);
        AdView adView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView2.loadAd(adRequest);

        IntroSlide introSlide = introSlides.get(position);

        signUpRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(context.getString(R.string.web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build())
                .build();

        if (position == introSlides.size() - 1) {
            Button startButton = view.findViewById(R.id.startButton);
            startButton.setVisibility(View.VISIBLE);
            EditText refCode = view.findViewById(R.id.refCodeView);
            refCode.setVisibility(View.VISIBLE);

            startButton.setOnClickListener(v -> {

                if (!refCode.getText().toString().isEmpty()) {
                    String ref = refCode.getText().toString();
                    checkRefExistence(ref, (exists, referrer) -> {
                        if (exists) {
                            this.refCode = ref;
                            this.referrer = referrer;
                            launchSignIn();
                        } else {
                            Toast.makeText(context, R.string.the_referral_code_is_not_valid, Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    launchSignIn();
                }
            });
        }

        titleTextView.setText(introSlide.getTitle());
        descriptionTextView.setText(introSlide.getDescription());

        container.addView(view);

        return view;
    }

    private void launchSignIn() {
        oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener((Activity) context, beginSignInResult -> {
                    IntentSenderRequest intentSenderRequest =
                            new IntentSenderRequest.Builder(beginSignInResult.getPendingIntent().getIntentSender()).build();
                    oneTapLauncher.launch(intentSenderRequest);
                })
                .addOnFailureListener((Activity) context, e -> Log.d("Login", e.getMessage()));
    }

    private void checkRefExistence(String code, CodeExistenceCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("referralCodes").document(code);

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                boolean exists = document.exists();
                String referrer = exists ? document.getString("uid") : "";
                callback.onCodeExistenceChecked(exists, referrer);
            } else {
                callback.onCodeExistenceChecked(false, "");
            }
        });
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return introSlides.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public String getRefCode() {
        return refCode;
    }

    public String getReferrer() {
        return referrer;
    }
}
