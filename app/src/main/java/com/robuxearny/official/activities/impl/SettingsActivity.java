/*
 * Created by FakeException on 8/11/23, 2:29 PM
 * Copyright (c) 2023. All rights reserved.
 * Last modified 8/8/23, 1:49 PM
 */

package com.robuxearny.official.activities.impl;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;
import com.robuxearny.official.adapters.FAQAdapter;
import com.robuxearny.official.callbacks.FAQCallback;
import com.robuxearny.official.models.FAQItem;
import com.robuxearny.official.utils.BackendUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar tbToolBar = findViewById(R.id.settings_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        Appodeal.setBannerViewId(R.id.appodealBannerView);
        Appodeal.show(this, Appodeal.BANNER_VIEW);
    }

    public void deleteAccount(View view) {
        showConfirmation(this);
    }

    private void showConfirmation(final Activity activity) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(activity);
        builder.setTitle(getString(R.string.confirmation));
        builder.setMessage(getString(R.string.confirm_desc2));
        builder.setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFunctions functions = FirebaseFunctions.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("uid", user.getUid());

                functions
                        .getHttpsCallable("deleteAccount")
                        .call(data)
                        .addOnSuccessListener(result -> {
                            // Handle success
                            FirebaseAuth.getInstance().signOut();
                            Toast.makeText(this, R.string.account_deleted, Toast.LENGTH_LONG).show();
                            getPrefsEditor().putInt("coins", 0).apply();
                            Intent main = new Intent(this, MainActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(main);
                        })
                        .addOnFailureListener(e -> {
                            // Handle failure
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                            Log.e("Cloud Function", "Error: " + e.getMessage());
                        });
            }
        });
        builder.setNegativeButton(R.string.no, ((dialogInterface, i) -> dialogInterface.cancel()));
        builder.show();
    }

    public void openGithub(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/fakeexception/RobuxEarny")));
    }

    public void joinDiscord(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/ZgW6hmAgxk")));
    }

    public void copyMail(View view) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

        if (clipboardManager != null) {
            ClipData clipData = ClipData.newPlainText("RobuxEarny Mail", "info@robuxrush.com");
            clipboardManager.setPrimaryClip(clipData);
        }
    }

    public void logoutAccount(View view) {
        FirebaseAuth.getInstance().signOut();
        Intent main = new Intent(this, MainActivity.class);
        main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        getPrefsEditor().putInt("coins", 0).apply();
        startActivity(main);
    }

    public void openFAQ(View view) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_faq, null);
        RecyclerView faqRecyclerView = popupView.findViewById(R.id.faqRecyclerView);
        faqRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        BackendUtils backendUtils = new BackendUtils(this);

        backendUtils.fetchFAQs(new FAQCallback() {
            @Override
            public void onFAQsLoaded(List<FAQItem> faqs) {
                FAQAdapter adapter = new FAQAdapter(faqs);
                faqRecyclerView.setAdapter(adapter);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle errors, e.g., display an error message
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

            }
        });// Create a dialog and set the custom layout
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setView(popupView);
        alertDialogBuilder.show();
    }

    public void sendFeedback(View view) {
        // Inflate the layout for the BottomSheetDialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_feedback, new FrameLayout(this), false);
        EditText feedbackEditText = dialogView.findViewById(R.id.feedbackEditText);

        // Create the BottomSheetDialog
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(dialogView);

        // Find the buttons in the layout
        Button submitButton = dialogView.findViewById(R.id.submitButton);
        Button cancelButton = dialogView.findViewById(R.id.cancelButton);

        // Apply properties to the feedback EditText
        feedbackEditText.setSingleLine(false);
        feedbackEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        feedbackEditText.setGravity(Gravity.TOP | Gravity.START);
        feedbackEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(512)});
        feedbackEditText.setHorizontallyScrolling(false);

        // Set click listener for the submit button
        submitButton.setOnClickListener(v -> {
            String feedbackText = feedbackEditText.getText().toString();

            if (!feedbackText.isEmpty()) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    FirebaseFunctions functions = FirebaseFunctions.getInstance();

                    Map<String, Object> data = new HashMap<>();
                    data.put("messageContent", "Id: " + user.getUid() + " Feedback: " + feedbackText);

                    functions
                            .getHttpsCallable("feedback")
                            .call(data)
                            .addOnSuccessListener(result -> {
                                // Handle success
                                Snackbar.make(view, R.string.feedback_sent, Snackbar.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                // Handle failure
                                Log.e("Cloud Function", "Error: " + e.getMessage());
                            });
                }
            }

            bottomSheetDialog.dismiss();
        });

        // Set click listener for the cancel button
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        // Show the dialog
        bottomSheetDialog.show();
    }
}
