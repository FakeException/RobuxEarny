/*
 * Created by FakeException on 8/5/23, 11:58 AM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/5/23, 11:58 AM
 */

package com.robuxearny.official.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.CancellationSignal;
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
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.CredentialManagerCallback;
import androidx.credentials.CustomCredential;
import androidx.credentials.GetCredentialRequest;
import androidx.credentials.GetCredentialResponse;
import androidx.credentials.exceptions.GetCredentialException;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.MainMenuActivity;
import com.robuxearny.official.callbacks.CodeExistenceCallback;
import com.robuxearny.official.models.IntroSlide;
import com.robuxearny.official.survey.AppsPrizeSurvey;
import com.robuxearny.official.utils.BackendUtils;
import com.robuxearny.official.utils.ReferralUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IntroSliderAdapter extends PagerAdapter {

    private final Activity context;
    private final List<IntroSlide> introSlides;
    private String refCode;
    private final FirebaseAuth mAuth;

    public IntroSliderAdapter(Activity context, List<IntroSlide> introSlides) {
        this.context = context;
        this.introSlides = introSlides;
        this.refCode = "";
        this.mAuth = FirebaseAuth.getInstance();
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public void handleSignIn(GetCredentialResponse result) {
        Credential credential = result.getCredential();

        if (credential instanceof CustomCredential) {
            if (GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL.equals(credential.getType())) {
                GoogleIdTokenCredential googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.getData());

                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.getIdToken(), null);

                mAuth.signInWithCredential(firebaseCredential)
                        .addOnCompleteListener(context.getMainExecutor(), task -> {
                            if (task.isSuccessful()) {
                                if (mAuth.getCurrentUser() != null) {
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    String uid = user.getUid();

                                    try {
                                        new AppsPrizeSurvey(context, uid);
                                    } catch (IOException | GooglePlayServicesRepairableException |
                                             GooglePlayServicesNotAvailableException e) {
                                        throw new RuntimeException(e);
                                    }

                                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();

                                    saveData(uid);
                                }

                            } else {
                                Toast.makeText(context, R.string.login_error, Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Log.e("Login", "Unexpected type of credential");
            }
        } else {
            // Catch any unrecognized credential type here.
            Log.e("Login", "Unexpected type of credential");
        }
    }

    private void saveData(String uid) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(uid);

        SharedPreferences preferences = context.getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("coins", 0);
                    userMap.put("ads", 0);
                    userMap.put("has4xBooster", false);
                    userMap.put("has10xBooster", false);

                    if (!getRefCode().isEmpty()) {
                        userMap.put("referralCode", getRefCode());
                    }

                    userDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User data saved successfully.");
                                editor.apply();

                                ReferralUtils.saveUserReferral(db, uid, (referralCode) -> {
                                    if (referralCode != null) {
                                        // Store in SharedPreferences
                                        editor.putString("myReferralCode", referralCode).apply();
                                        Log.d("Referral", "Your referral code is: " + referralCode);
                                    } else {
                                        // Handle the case where no referral code was found
                                        Log.w("Referral", "No referral code found for the user.");
                                    }

                                    Intent intent = new Intent(context, MainMenuActivity.class);
                                    context.startActivity(intent);
                                });
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data: " + e.getMessage()));
                } else {
                    BackendUtils.retrieveMoney(context, () -> ReferralUtils.saveUserReferral(db, uid, (referralCode) -> {
                        if (referralCode != null) {
                            // Store in SharedPreferences
                            editor.putString("myReferralCode", referralCode).apply();
                            Log.d("Referral", "Your referral code is: " + referralCode);
                        } else {
                            // Handle the case where no referral code was found
                            Log.w("Referral", "No referral code found for the user.");
                        }

                        Intent intent = new Intent(context, MainMenuActivity.class);
                        context.startActivity(intent);
                    }));
                }
            }
        });
    }



    private void authentication() {
        CredentialManager credentialManager = CredentialManager.create(this.context);
        GetGoogleIdOption googleIdOption = new GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(context.getString(R.string.web_client_id))
                .setAutoSelectEnabled(true)
                .build();

        GetCredentialRequest request = new GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build();



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            credentialManager.getCredentialAsync(context, request, new CancellationSignal(), context.getMainExecutor(),
                    new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                        @Override
                        public void onResult(GetCredentialResponse getCredentialResponse) {
                            handleSignIn(getCredentialResponse);
                            // Re-enable the button and hide the progress bar

                        }

                        @Override
                        public void onError(@NonNull GetCredentialException e) {
                            Log.e("Login", Objects.requireNonNull(e.getMessage()));

                            Toast.makeText(context, e.getMessage() + ", Ensure you have a Google account linked in your device.", Toast.LENGTH_SHORT).show();

                            // Re-enable the button and hide the progress bar (in case of error)

                        }
                    });
        }
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_intro_slide, container, false);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView descriptionTextView = view.findViewById(R.id.descriptionTextView);

        IntroSlide introSlide = introSlides.get(position);

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
        // Inflate the layout for the BottomSheetDialog
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_referral_code, new FrameLayout(context), false);
        EditText referralEditText = dialogView.findViewById(R.id.referralEditText);

        // Apply properties and behavior from getEditText()
        referralEditText.setSingleLine(false);
        referralEditText.setHint(R.string.refcode_desc);
        referralEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        referralEditText.setGravity(Gravity.TOP | Gravity.START);
        referralEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        referralEditText.addTextChangedListener(new TextWatcher() {
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
                    referralEditText.setText(text);
                    referralEditText.setSelection(text.length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        // Create the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(dialogView);

        // Find the buttons in the layout
        Button confirmButton = dialogView.findViewById(R.id.confirmButton);
        Button skipButton = dialogView.findViewById(R.id.skipButton);

        // Set click listener for the confirm button
        confirmButton.setOnClickListener(v -> {
            String refText = referralEditText.getText().toString();
            bottomSheetDialog.dismiss();

            if (!refText.isEmpty()) {
                checkRefExistence(refText, (exists, referrer) -> {
                    if (exists) {
                        this.refCode = refText;
                        authentication();
                    } else {
                        Toast.makeText(context, R.string.the_referral_code_is_not_valid, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                authentication();
            }
        });

        // Set click listener for the skip button
        skipButton.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            authentication();
        });

        // Show the dialog
        bottomSheetDialog.show();
    }

    private @NonNull EditText getEditText() {
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
        return referallEditText;
    }

    private void checkRefExistence(String code, CodeExistenceCallback callback) {
        String sanitizedCode = code.replaceAll("[^a-zA-Z0-9]", ""); // Allow only letters and numbers

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("referralCodes").document(sanitizedCode);

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
}
