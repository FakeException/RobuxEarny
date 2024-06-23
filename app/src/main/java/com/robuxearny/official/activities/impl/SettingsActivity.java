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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.robuxearny.official.R;
import com.robuxearny.official.activities.BaseActivity;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar tbToolBar = findViewById(R.id.settings_tb_toolbar);
        tbToolBar.setNavigationOnClickListener(v -> finish());

        Appodeal.show(this, Appodeal.BANNER_BOTTOM);
        Appodeal.show(this, Appodeal.BANNER_TOP);
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
                            SharedPreferences.Editor editor = getPrefsEditor();
                            editor.putInt("coins", 0).apply();
                            Intent main = new Intent(this, MainActivity.class);
                            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(main);
                            finish();
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

        SharedPreferences.Editor editor = getPrefsEditor();
        editor.putInt("coins", 0).apply();
        startActivity(main);
        finish();
    }

    public void openFAQ(View view) {
        View popupView = getLayoutInflater().inflate(R.layout.popup_faq, null);
        popupView.setPadding(32, 32, 32, 32);

        // Create a dialog and set the custom layout
        MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(this);
        alertDialogBuilder.setView(popupView);
        alertDialogBuilder.show();
    }

    public void sendFeedback(View view) {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle(R.string.send_feedback);

        // Create the layout for the dialog
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 0);

        // Create an EditText for the feedback input
        final EditText feedbackEditText = new EditText(this);
        feedbackEditText.setHint(R.string.feedback_desc);

        feedbackEditText.setSingleLine(false);
        feedbackEditText.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
        feedbackEditText.setGravity(Gravity.TOP | Gravity.START);
        feedbackEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(512)});
        feedbackEditText.setHorizontallyScrolling(false);

        layout.addView(feedbackEditText);


        builder.setView(layout);

        // Add a Submit button
        builder.setPositiveButton(R.string.submit, (dialog, which) -> {
            String feedbackText = feedbackEditText.getText().toString();

            if (feedbackText.isEmpty()) return;

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                FirebaseFunctions functions = FirebaseFunctions.getInstance();

                Map<String, Object> data = new HashMap<>();
                data.put("messageContent", "Email: " + user.getEmail() + " Feedback: " + feedbackText);

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

            dialog.dismiss();
        });

        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}
