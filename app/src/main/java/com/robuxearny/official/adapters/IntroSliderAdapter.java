/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
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
            startButton.setOnClickListener(v -> codeInput());
        }

        titleTextView.setText(introSlide.getTitle());
        descriptionTextView.setText(introSlide.getDescription());

        container.addView(view);

        return view;
    }

    public void codeInput() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        builder.setTitle(R.string.ref_code);

        // Create the layout for the dialog
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 0);

        // Create an EditText for the feedback input
        final EditText referallEditText = new EditText(context);

        referallEditText.setSingleLine(false);
        referallEditText.setHint(R.string.refcode_desc);
        referallEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        referallEditText.setGravity(Gravity.TOP | Gravity.START);

        referallEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        referallEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Convert the text to uppercase
                String text = s.toString().toUpperCase();

                if (text.length() > 8) {
                    text = text.substring(0, 8);
                }

                if (!text.equals(s.toString())) {
                    referallEditText.setText(text);
                    referallEditText.setSelection(text.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        layout.addView(referallEditText);

        builder.setView(layout);

        // Add a Submit button
        builder.setPositiveButton(R.string.confirmation, (dialog, which) -> {
            String refText = referallEditText.getText().toString();
            dialog.dismiss();

            if (!refText.isEmpty()) {

                checkRefExistence(refText, (exists, referrer) -> {
                    if (exists) {
                        this.refCode = refText;
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

        builder.setNegativeButton(R.string.i_don_t_have_a_code, (dialog, which) -> {
            dialog.dismiss();
            launchSignIn();
        });

        builder.setCancelable(false);

        builder.show();
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
