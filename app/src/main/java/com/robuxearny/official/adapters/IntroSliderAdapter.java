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
import android.widget.LinearLayout;
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

import com.appodeal.ads.Appodeal;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.impl.MainMenuActivity;
import com.robuxearny.official.data.IntroSlide;
import com.robuxearny.official.utils.CodeExistenceCallback;
import com.robuxearny.official.utils.ReferralCodeGenerator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class IntroSliderAdapter extends PagerAdapter {

    private final Context context;
    private final List<IntroSlide> introSlides;
    private String refCode;
    private String referrer;
    private final FirebaseAuth mAuth;

    public IntroSliderAdapter(Context context, List<IntroSlide> introSlides) {
        this.context = context;
        this.introSlides = introSlides;
        this.refCode = "";
        this.referrer = "";
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
                                    Toast.makeText(context, R.string.login_success, Toast.LENGTH_SHORT).show();

                                    if (!getRefCode().isEmpty()) {
                                        saveData(user.getUid(), 100);
                                    } else {
                                        saveData(user.getUid(), 0);
                                    }

                                    Intent intent = new Intent(context, MainMenuActivity.class);
                                    context.startActivity(intent);
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

    private void saveData(String uid, int coinAmount) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDocRef = db.collection("users").document(uid);

        SharedPreferences preferences = context.getSharedPreferences("RobuxEarny", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        userDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    String referral = ReferralCodeGenerator.generateReferralCode();
                    userMap.put("uid", uid);
                    userMap.put("coins", coinAmount);
                    userMap.put("referral", referral);
                    userMap.put("ads", 0);
                    userMap.put("has4xBooster", false);
                    userMap.put("has10xBooster", false);

                    editor.putString("referralCode", referral);
                    editor.apply();

                    userDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "User data saved successfully.");
                                saveReferral(uid, referral);

                                if (!getRefCode().isEmpty()) {
                                    if (!getReferrer().equals(uid)) {
                                        updateCoins(getReferrer(), 200);
                                    }
                                }

                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data: " + e.getMessage()));
                } else {

                    editor.putString("referralCode", document.getString("referral"));

                    Long coinsLong = document.getLong("coins");
                    if (coinsLong != null) {
                        long coins = coinsLong;
                        editor.putInt("coins", (int) coins);
                    }

                    editor.apply();
                }
            }
        });
    }

    public void updateCoins(String uid, int newCoins) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(uid);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    userRef.update("coins", FieldValue.increment(newCoins))
                            .addOnSuccessListener(obj -> Log.d("Coins", "Coins updated"))
                            .addOnFailureListener(exc -> Log.d("Coins", exc.getMessage()));
                }
            }

        });

    }

    private void saveReferral(String uid, String code) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference codeDocRef = db.collection("referralCodes").document(code);

        codeDocRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (!document.exists()) {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);

                    codeDocRef.set(userMap)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "User data saved successfully."))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving user data: " + e.getMessage()));
                }
            }
        });
    }

    public void authentication() {
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
            credentialManager.getCredentialAsync(context, request, new CancellationSignal(), context.getMainExecutor(), new CredentialManagerCallback<GetCredentialResponse, GetCredentialException>() {
                @Override
                public void onResult(GetCredentialResponse getCredentialResponse) {
                    handleSignIn(getCredentialResponse);
                }

                @Override
                public void onError(@NonNull androidx.credentials.exceptions.GetCredentialException e) {
                    Log.e("Login", Objects.requireNonNull(e.getMessage()));
                }
            });
        }
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.intro_slide, container, false);

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

        Appodeal.show((Activity) context, Appodeal.BANNER_BOTTOM); // Display banner at the bottom of the screen
        Appodeal.show((Activity) context, Appodeal.BANNER_TOP);    // Display banner at the top of the screen

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
        final EditText referallEditText = getEditText();

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
                        authentication();
                    } else {
                        Toast.makeText(context, R.string.the_referral_code_is_not_valid, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                authentication();
            }
        });

        builder.setNegativeButton(R.string.i_don_t_have_a_code, (dialog, which) -> {
            dialog.dismiss();
            authentication();
        });

        builder.setCancelable(false);

        builder.show();
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
